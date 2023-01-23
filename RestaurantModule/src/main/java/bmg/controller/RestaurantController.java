package bmg.controller;

import bmg.dto.Address;
import bmg.dto.Restaurant;
import bmg.dto.RestaurantFilters;
import bmg.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles requests related to {@link Restaurant}s
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    @Qualifier("yelp")
    private RestaurantService yelp;

    @Autowired
    @Qualifier("google")
    private RestaurantService google;

    /**
     * Gets a list of restaurants matching the given parameters
     *
     * @param source Determines the source of the list (Yelp or Google)
     * @param address1 An address line 1 to search
     * @param address2 An address line 2 to search
     * @param city A city to search
     * @param stateProvince A state or province to search
     * @param postalCode A postal code to search
     * @param country A country to search
     * @param radius The radius (in meters) around which to search
     * @param keywords A list of keywords to search
     * @param maxPrice The maximum price level
     * @param openNow True if the restaurant is open at the time of the search
     * @return A list of restaurants matching the given parameters
     */
    @GetMapping(value = {"", "/{source}"})
    public ResponseEntity<Response2<List<Restaurant>>> getAll(
            @PathVariable(required = false) String source,
            @RequestParam(required = false) String address1,
            @RequestParam(required = false) String address2,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String stateProvince,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) String[] keywords,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Boolean openNow
    ) {

        Address address = new Address(address1, address2, city, stateProvince, postalCode, country);
        verifyAddressNotNull(address);

        RestaurantFilters filters =
                RestaurantFilters
                        .builder()
                        .address(address)
                        .radius(radius)
                        .keywords(keywords)
                        .maxPrice(maxPrice)
                        .openNow(openNow)
                        .build();

        List<Restaurant> data;
        HttpStatus status;
        String message;

        if (source == null)
            source = "yelp";

        try {
            switch (source) {
                case "google":
                    data = google.getRestaurants(filters);
                    break;

                case "yelp":
                default:
                    data = yelp.getRestaurants(filters);
                    break;
            }
            status = HttpStatus.OK;
            message = status.name();

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = e.getMessage();
            data = new ArrayList<>();
        }

        Response2<List<Restaurant>> response = new Response2<>();
        response.setStatus(status.value());
        response.setMessage(message);
        response.setData(data);
        response.setPath(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Checks that at least one address field is not blank
     *
     * @param address An address to verify
     * @throws IllegalArgumentException If all address fields are blank
     */
    private void verifyAddressNotNull(Address address) throws IllegalArgumentException {
        if (address.getAddressString().isBlank())
            throw new IllegalArgumentException("At least one address field is required.");
    }
}
