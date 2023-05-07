package bmg.service;

import bmg.dto.Coordinates;
import bmg.dto.Place;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;
@RequiredArgsConstructor

@Service
public class PlaceService {

    @Value("${key.google}")
    private String googlekey;
    @Value("${api.nearbySearch}")
    private String googlePlacesAPIendpoint;

    private final CoordinatesService CS;

    /**
     * Retrieves a List of Places by a provided paramater address that we convert to a Coordinate
     * pair, and call the GooglePlaces API for.
     * @param address from property address or any zipcode we can generate a list of Places for
     * @return a List of Places
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public List<Place> getPlaces(String address) throws IOException, InterruptedException, ExecutionException {
        Coordinates c = CS.getCoordinates(address);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(
                        googlePlacesAPIendpoint + "?location=%s,%s&radius=500&type=attraction&key=" + googlekey,
                        c.getLatitude(), c.getLongitude())))
                        .build();

        String response = client
                .send(request, HttpResponse.BodyHandlers.ofString())
                .body();

        return convertToList(response);
    }

    /**
     * Converts GooglePlaces API Nearby Search response to a List of Places
     *
     * @param response The response from Google Places API (NearbySearch)
     * @return A List of Places
     * @throws JsonProcessingException If the json object could not be processed
     */
    private List<Place> convertToList(String response)
            throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new SimpleModule().addDeserializer(Place.class, new PlaceDeserializer()));

        return StreamSupport
                .stream(mapper.readTree(response).get("results").spliterator(), false)
                .map((place) -> mapper.convertValue(place, Place.class))
                .toList();
    }
}