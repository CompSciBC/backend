package bmg.service;

import bmg.dto.Restaurant;
import bmg.dto.RestaurantFilters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Provides services for locating {@link Restaurant}s
 */
public interface RestaurantService {

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
    public abstract List<Restaurant> getRestaurants(RestaurantFilters filters)
            throws URISyntaxException, IllegalArgumentException, IOException, InterruptedException;
}
