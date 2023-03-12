package bmg.service;

import bmg.dto.Restaurant;
import bmg.dto.RestaurantFilters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Locates {@link Restaurant}s using the Yelp Fusion API
 */
@Service
public class RestaurantServiceYelp implements RestaurantService {

    private final HttpClient CLIENT;
    private final ObjectMapper MAPPER;
    private final String AUTHORIZATION_HEADER;
    private final String ENDPOINT;

    public RestaurantServiceYelp(@Value("${key.yelp}") String apiKey,
                                 @Value("${api.yelpBusinessSearchGraphQL}") String endpoint) {

        this.CLIENT = HttpClient.newHttpClient();
        this.AUTHORIZATION_HEADER = "Bearer "+apiKey;
        this.ENDPOINT = endpoint;
        this.MAPPER = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new SimpleModule()
                        .addDeserializer(Restaurant.class, new RestaurantDeserializerYelp()));
    }

    @Override
    public List<Restaurant> getRestaurants(RestaurantFilters filters)
            throws URISyntaxException, IllegalArgumentException, IOException, InterruptedException {

        StringBuilder search = new StringBuilder("categories: \\\"restaurants\\\"");
        search.append(String.format(", location: \\\"%s\\\"", filters.getAddress().getAddressString()));

        Integer radius = filters.getRadius(); // default to 40km radius
        search.append(String.format(", radius: %d", radius != null && radius > 0 ? radius : 40_000));

        String[] keywords = filters.getKeywords();
        if (keywords != null) {
            String keywordString = Arrays.toString(filters.getKeywords()).replaceAll("[\\[\\]]", "").trim();

            if (!keywordString.equals(""))
                search.append(String.format(", term: \\\"%s\\\"", keywordString));
        }

        Integer maxPrice = filters.getMaxPrice();
        if (maxPrice != null) {
            StringBuilder priceList = new StringBuilder();

            // yelp max price is 4
            for (int i = 1; i <= Math.min(maxPrice, 4); i++)
                priceList.append(priceList.isEmpty() ? "" : ",").append(i);

            search.append(String.format(", price: \\\"%s\\\"", priceList));
        }

        Boolean openNow = filters.getOpenNow();
        if (openNow != null) search.append(String.format(", open_now: %b", openNow));

        Integer numResults = filters.getNumResults(); // default to 30 results if null
        search.append(String.format(", limit: %d", numResults != null && numResults > 0 ? numResults : 30));

        String query = String.format("""
                query {
                  search(%s) {
                    total
                    business {
                      id
                      name
                      phone
                      display_phone
                      distance
                      hours {
                        hours_type
                        is_open_now
                        open {
                          day
                          end
                          is_overnight
                          start
                        }
                      }
                      categories {
                        title
                        alias
                      }
                      coordinates {
                        latitude
                        longitude
                      }
                      location {
                        address1
                        address2
                        address3
                        city
                        state
                        country
                        postal_code
                        formatted_address
                      }
                      photos
                      price
                      rating
                      review_count
                      url
                    }
                  }
                }
                """, search);

        return post(query);
    }

    /**
     * Performs a POST request with the given query
     *
     * @param query The query to send
     * @return A list of restaurants
     * @throws URISyntaxException If the URI cannot be formed
     * @throws IOException If an I/O error occurs when sending or receiving
     * @throws InterruptedException If the operation is interrupted
     */
    private List<Restaurant> post(String query) throws URISyntaxException, IOException, InterruptedException {
        // replace new line characters since they are not allowed
        String formattedQuery = query.replaceAll("\\s+", " ");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(new URI(ENDPOINT))
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString("{ \"query\": \"" + formattedQuery + "\" }"))
                .build();

        String response = CLIENT
                .send(request, HttpResponse.BodyHandlers.ofString())
                .body();

        return convertToList(response);
    }

    /**
     * Converts the API response to a list of restaurants
     *
     * @param response The response from the API
     * @return A list of restaurants
     * @throws JsonProcessingException If the json object could not be processed
     */
    private List<Restaurant> convertToList(String response) throws JsonProcessingException {
        return StreamSupport
                .stream(MAPPER
                        .readTree(response)
                        .get("data")
                        .get("search")
                        .get("business")
                        .spliterator(), false)
                .map((business) -> MAPPER.convertValue(business, Restaurant.class))
                .toList();
    }
}
