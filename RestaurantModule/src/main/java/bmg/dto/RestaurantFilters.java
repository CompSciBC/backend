package bmg.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Specifies filters for a {@link Restaurant} search
 */
@Data
@Builder
public class RestaurantFilters {
    private Address address;
    private Integer radius;
    private String[] keywords;
    private Integer maxPrice;
    private Boolean openNow;
    private Integer numResults;
}
