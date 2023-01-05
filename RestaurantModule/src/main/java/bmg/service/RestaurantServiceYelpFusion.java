package bmg.service;

import bmg.dto.Address;
import bmg.dto.Restaurant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Locates {@link Restaurant}s using the Yelp Fusion API
 */
@Service
@Qualifier("yelp")
public class RestaurantServiceYelpFusion extends RestaurantService {

    @Value("${api.yelpBusinessSearch}")
    private String endpoint;

    public RestaurantServiceYelpFusion(@Value("${key.yelp}") String key) {
        super(
                "Bearer " + key,
                "radius",
                "term",
                "price",
                "open_now",
                "businesses",
                new RestaurantDeserializerYelpFusion()
        );
    }
    @Override
    protected String getBaseUrl(Address address) {
        return endpoint
                + "?location=" + URLEncoder.encode(address.getAddressString(), StandardCharsets.UTF_8)
                + "&categories=restaurants";
    }
}
