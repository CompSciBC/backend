package bmg.dto;

import java.util.Arrays;
import java.util.Objects;

/**
 * Specifies filters for a restaurant search
 */
public class RestaurantFilters {
    private Address address;
    private Integer radius;
    private String[] keywords;
    private Integer maxPrice;
    private Boolean openNow;

    /**
     * A builder of {@link RestaurantFilters} objects
     */
    public static class RestaurantFiltersBuilder {
        private Address address;
        private Integer radius;
        private String[] keywords;
        private Integer maxPrice;
        private Boolean openNow;

        public RestaurantFiltersBuilder address(Address address) {
            this.address = address;
            return this;
        }

        public RestaurantFiltersBuilder radius(Integer radius) {
            this.radius = radius;
            return this;
        }

        public RestaurantFiltersBuilder keywords(String[] keywords) {
            this.keywords = keywords;
            return this;
        }

        public RestaurantFiltersBuilder maxPrice(Integer maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public RestaurantFiltersBuilder openNow(Boolean openNow) {
            this.openNow = openNow;
            return this;
        }

        public RestaurantFilters build() {
            return new RestaurantFilters(
                    this.address,
                    this.radius,
                    this.keywords,
                    this.maxPrice,
                    this.openNow
            );
        }
    }

    public static RestaurantFiltersBuilder builder() {
        return new RestaurantFiltersBuilder();
    }

    public RestaurantFilters() {
    }

    public RestaurantFilters(Address address, Integer radius, String[] keywords,
                             Integer maxPrice, Boolean openNow) {

        this.address = address;
        this.radius = radius;
        this.keywords = keywords;
        this.maxPrice = maxPrice;
        this.openNow = openNow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof RestaurantFilters that))
            return false;

        return address.equals(that.address)
                && Objects.equals(radius, that.radius)
                && Arrays.equals(keywords, that.keywords)
                && Objects.equals(maxPrice, that.maxPrice)
                && Objects.equals(openNow, that.openNow);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(address, radius, maxPrice, openNow);
        result = 31 * result + Arrays.hashCode(keywords);
        return result;
    }

    @Override
    public String toString() {
        return "RestaurantFilters{" +
                "address=" + address +
                ", radius=" + radius +
                ", keywords=" + Arrays.toString(keywords) +
                ", maxPrice=" + maxPrice +
                ", openNow=" + openNow +
                '}';
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }
}
