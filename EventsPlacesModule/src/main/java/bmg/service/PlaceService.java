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
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;
@RequiredArgsConstructor

//@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@Service
public class PlaceService {

    @Value("${key.google}")
    private String googlekey;
    @Value("${api.nearbySearch}")
    private String googlePlacesAPIendpoint;

    @Value("${api.CoverPhotoReference}")
    private String googlePlacesAPIphoto;

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
    public List<Place> getPlaces(String address) throws IOException, InterruptedException, ExecutionException, SQLException {
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
            throws IOException, InterruptedException, SQLException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new SimpleModule().addDeserializer(Place.class, new PlaceDeserializer()));

        List<Place> placesList = StreamSupport
                .stream(mapper.readTree(response).get("results").spliterator(), false)
                .map((place) -> mapper.convertValue(place, Place.class))
                .toList();


        for (int i = 0; i < placesList.size(); i++) {
            String url = String.format(googlePlacesAPIphoto + "?maxwidth=600&photoreference=" + placesList.get(i).getUserPhotoReference() +"&key=" + googlekey);
            placesList.get(i).setPhoto(getBlobFromUrl(url));

        }
        return placesList;
    }
    public Blob getBlobFromUrl(String url) throws IOException, InterruptedException, SQLException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        int bytesRead;
        byte[] buffer = new byte[1024];
        while ((bytesRead = response.body().read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        Blob blob = new javax.sql.rowset.serial.SerialBlob(baos.toByteArray());
        System.out.println(url);
        System.out.println(blob);
        return blob;
    }
}