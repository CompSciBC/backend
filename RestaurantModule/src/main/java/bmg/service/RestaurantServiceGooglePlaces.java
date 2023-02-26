package bmg.service;

import bmg.dto.Address;
import bmg.dto.Coordinates;
import bmg.dto.Restaurant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Locates {@link Restaurant}s using the Google PLaces API
 */
@Service
@Qualifier("google")
public class RestaurantServiceGooglePlaces extends RestaurantService {

    @Value("${key.googlePlaces}")
    private String key;

    @Value("${api.nearbySearch}")
    private String endpoint;

    public RestaurantServiceGooglePlaces() {
        super(
                "",
                "radius",
                "keyword",
                "maxprice",
                "opennow",
                null,
                "results",
                new RestaurantDeserializerGooglePlaces()
        );
    }

    @Override
    protected String getBaseUrl(Address address) {
        // TODO: convert address to coordinate; this should be a service in location module
        Coordinates latLong = new Coordinates(47.6062, -122.3321); // this is Seattle

        return endpoint
                + "?location=" + latLong.getURLEncoded()
                + "&type=restaurant"
                + "&key=" + key;
    }
}
