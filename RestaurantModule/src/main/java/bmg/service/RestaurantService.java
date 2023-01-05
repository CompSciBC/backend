package bmg.service;

import bmg.dto.Address;
import bmg.dto.Restaurant;
import bmg.dto.RestaurantFilters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Provides services for locating {@link Restaurant}s
 */
public abstract class RestaurantService {

    private final HttpClient CLIENT;
    private final String AUTHORIZATION_HEADER;
    private final String RADIUS_KEY;
    private final String KEYWORDS_KEY;
    private final String MAX_PRICE_KEY;
    private final String OPEN_NOW_KEY;
    private final String RESPONSE_ROOT_FIELD;
    private final ObjectMapper MAPPER;

    public RestaurantService(String authorizationHeader, String radiusKey, String keywordsKey, String maxPriceKey,
                             String openNowKey, String responseRootField, StdDeserializer<Restaurant> deserializer) {

        this.CLIENT = HttpClient.newHttpClient();
        this.AUTHORIZATION_HEADER = authorizationHeader;
        this.RADIUS_KEY = radiusKey;
        this.KEYWORDS_KEY = keywordsKey;
        this.MAX_PRICE_KEY = maxPriceKey;
        this.OPEN_NOW_KEY = openNowKey;
        this.RESPONSE_ROOT_FIELD = responseRootField;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new SimpleModule().addDeserializer(Restaurant.class, deserializer));
        this.MAPPER = mapper;
    }

    /**
     * Gets a list of restaurants within the specified radius of the given address
     *
     * @param address An address to search around; must not be null
     * @param radius The radius (in meters) to search around the given address
     * @return A list of restaurants nearby the given address
     * @throws URISyntaxException If the URI cannot be formed
     * @throws IOException If an I/O error occurs when sending or receiving
     * @throws InterruptedException If the operation is interrupted
     */
    public List<Restaurant> getRestaurants(Address address, Integer radius)
            throws URISyntaxException, IOException, InterruptedException {

        return get(getUrlForRestaurantsByAddress(address, radius));
    }

    /**
     * Gets a list of restaurants based on the given restaurant filters
     *
     * @param filters A set of {@link RestaurantFilters} to apply to the search
     * @return A list of restaurants based on the given restaurant filters
     * @throws URISyntaxException If the URI cannot be formed
     * @throws IllegalArgumentException If filters is null
     * @throws IOException If an I/O error occurs when sending or receiving
     * @throws InterruptedException If the operation is interrupted
     */
    public List<Restaurant> getRestaurants(RestaurantFilters filters)
            throws URISyntaxException, IllegalArgumentException, IOException, InterruptedException {

        if (filters == null)
            throw new IllegalArgumentException("Filters cannot be null.");

        String url = getUrlForRestaurantsByAddress(filters.getAddress(), filters.getRadius());
        url += getParameter(KEYWORDS_KEY, Arrays.toString(filters.getKeywords()).replaceAll("[\\[\\]]", ""));
        url += getParameter(MAX_PRICE_KEY, filters.getMaxPrice());
        url += getParameter(OPEN_NOW_KEY, filters.getOpenNow());

        return get(url);
    }

    /**
     * Performs a GET request on the given URL
     *
     * @param url The URL to send a GET request to
     * @return A list of restaurants
     * @throws URISyntaxException If the URI cannot be formed
     * @throws IOException If an I/O error occurs when sending or receiving
     * @throws InterruptedException If the operation is interrupted
     */
    private List<Restaurant> get(String url) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request =
                HttpRequest
                        .newBuilder()
                        .GET()
                        .uri(new URI(url))
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER)
                        .build();

        String response = CLIENT
                .send(request, HttpResponse.BodyHandlers.ofString())
                .body();

        return convertToList(response);
    }

    /**
     * Converts the given parameter key and value into query string format (&key=value)
     *
     * @param key The name of the query string parameter key
     * @param value The value of the query string parameter
     * @return A query string parameter in format: &key=value
     */
    private String getParameter(String key, Object value) {
        return value != null && !value.toString().equalsIgnoreCase("null")
                ? "&" + key + "=" + URLEncoder.encode(value.toString(), StandardCharsets.UTF_8)
                : "";
    }

    /**
     * Creates a URL for a search based on the given address and radius
     *
     * @param address An address to search around; must not be null
     * @param radius The radius (in meters) to search around the given address
     * @return A URL for a search based on the given address and radius
     * @throws IllegalArgumentException if address is null
     */
    private String getUrlForRestaurantsByAddress(Address address, Integer radius) throws IllegalArgumentException {
        if (address == null)
            throw new IllegalArgumentException("Address cannot be null.");

        return getBaseUrl(address)
                + ("&" + RADIUS_KEY + "=" + (radius != null && radius >= 0 ? radius : 40_000));
    }

    /**
     * Creates a URL with the required parameters for a search for restaurants
     *
     * @param address An address to search around
     * @return A URL with the required parameters for a search for restaurants
     */
    protected abstract String getBaseUrl(Address address);

    /**
     * Converts the 3rd party API response to a list of restaurants
     *
     * @param response The response from the 3rd party restaurant API
     * @return A list of restaurants
     * @throws JsonProcessingException If the json object could not be processed
     */
    private List<Restaurant> convertToList(String response)
            throws JsonProcessingException {

        return StreamSupport
                .stream(MAPPER.readTree(response).get(RESPONSE_ROOT_FIELD).spliterator(), false)
                .map((business) -> MAPPER.convertValue(business, Restaurant.class))
                .toList();
    }
}
