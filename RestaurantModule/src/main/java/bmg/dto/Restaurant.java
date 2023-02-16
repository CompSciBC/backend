package bmg.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a restaurant
 */
@Data
@Builder
public class Restaurant {
    private String id;
    private String alias;
    private String name;
    private String imageUrl;
    private Boolean isOpen;
    private String url;
    private Integer numReviews;
    private String[] categories;
    private Double rating;
    private Coordinates coordinates;
    private String[] transactions;
    private Double price;
    private Address address;
    private String description;
    private String phone;
    private String displayPhone;
    private Double distance;
}
