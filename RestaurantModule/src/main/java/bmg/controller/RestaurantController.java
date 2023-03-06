package bmg.controller;

import bmg.dto.Address;
import bmg.dto.Restaurant;
import bmg.dto.RestaurantFilters;
import bmg.service.RestaurantService;
import lombok.RequiredArgsConstructor;
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
@CrossOrigin
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService SVC;

    /**
     * Gets a list of restaurants matching the given parameters
     *
     * @param line1 An address line 1 to search
     * @param line2 An address line 2 to search
     * @param city A city to search
     * @param stateProvince A state or province to search
     * @param postalCode A postal code to search
     * @param country A country to search
     * @param radius The radius (in meters) around which to search
     * @param keywords A list of keywords to search
     * @param maxPrice The maximum price level
     * @param openNow True if the restaurant is open at the time of the search
     * @param numResults The maximum number of results to return
     * @return A list of restaurants matching the given parameters
     */
    @GetMapping("")
    public ResponseEntity<Response2<List<Restaurant>>> getAll(
            @RequestParam(required = false) String line1,
            @RequestParam(required = false) String line2,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String stateProvince,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) String[] keywords,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Boolean openNow,
            @RequestParam(required = false) Integer numResults
    ) {

        Address address = new Address(line1, line2, city, stateProvince, postalCode, country);
        verifyAddressNotNull(address);

        RestaurantFilters filters =
                RestaurantFilters
                        .builder()
                        .address(address)
                        .radius(radius)
                        .keywords(keywords)
                        .maxPrice(maxPrice)
                        .openNow(openNow)
                        .numResults(numResults)
                        .build();

        List<Restaurant> data;
        HttpStatus status;
        String message;

        try {
            data = SVC.getRestaurants(filters);
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
