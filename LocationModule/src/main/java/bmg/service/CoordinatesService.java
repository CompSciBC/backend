package bmg.service;
import bmg.dto.Coordinates;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.concurrent.ExecutionException;

@Service
public class CoordinatesService {

    @Value("${key.google-geocoding}")
    private String key;//API key

    public Coordinates getCoordinates(String address) throws ExecutionException, InterruptedException {
        //return Coordinates for Space Needle (lat: 47.6205; lon: 122.3493);
        try {
            // System.out.println(key);
            GeoApiContext context = new GeoApiContext.Builder().apiKey(key).build();
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();

               
            return new Coordinates(results[0].geometry.location.lat, results[0].geometry.location.lng);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}


