package bmg.dto;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a restaurant
 */
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

    public static class RestaurantBuilder {
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

        public RestaurantBuilder id(String id) {
            this.id = id;
            return this;
        }

        public RestaurantBuilder alias(String alias) {
            this.alias = alias;
            return this;
        }

        public RestaurantBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RestaurantBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public RestaurantBuilder isOpen(Boolean isOpen) {
            this.isOpen = isOpen;
            return this;
        }

        public RestaurantBuilder url(String url) {
            this.url = url;
            return this;
        }

        public RestaurantBuilder numReviews(Integer numReviews) {
            this.numReviews = numReviews;
            return this;
        }

        public RestaurantBuilder categories(String[] categories) {
            this.categories = categories;
            return this;
        }

        public RestaurantBuilder rating(Double rating) {
            this.rating = rating;
            return this;
        }

        public RestaurantBuilder coordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public RestaurantBuilder transactions(String[] transactions) {
            this.transactions = transactions;
            return this;
        }

        public RestaurantBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public RestaurantBuilder address(Address address) {
            this.address = address;
            return this;
        }

        public RestaurantBuilder description(String description) {
            this.description = description;
            return this;
        }

        public RestaurantBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public RestaurantBuilder displayPhone(String displayPhone) {
            this.displayPhone = displayPhone;
            return this;
        }

        public RestaurantBuilder distance(Double distance) {
            this.distance = distance;
            return this;
        }

        public Restaurant build() {
            return new Restaurant(
                    this.id,
                    this.alias,
                    this.name,
                    this.imageUrl,
                    this.isOpen,
                    this.url,
                    this.numReviews,
                    this.categories,
                    this.rating,
                    this.coordinates,
                    this.transactions,
                    this.price,
                    this.address,
                    this.description,
                    this.phone,
                    this.displayPhone,
                    this.distance
            );
        }
    }

    public static RestaurantBuilder builder() {
        return new RestaurantBuilder();
    }

    public Restaurant() {
    }

    public Restaurant(String id, String alias, String name, String imageUrl, Boolean isOpen, String url,
                      Integer numReviews, String[] categories, Double rating, Coordinates coordinates,
                      String[] transactions, Double price, Address address, String description, String phone,
                      String displayPhone, Double distance) {

        this.id = id;
        this.alias = alias;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isOpen = isOpen;
        this.url = url;
        this.numReviews = numReviews;
        this.categories = categories;
        this.rating = rating;
        this.coordinates = coordinates;
        this.transactions = transactions;
        this.price = price;
        this.address = address;
        this.description = description;
        this.phone = phone;
        this.displayPhone = displayPhone;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Restaurant that))
            return false;

        return Objects.equals(id, that.id)
                && Objects.equals(alias, that.alias)
                && Objects.equals(name, that.name)
                && Objects.equals(imageUrl, that.imageUrl)
                && Objects.equals(isOpen, that.isOpen)
                && Objects.equals(url, that.url)
                && Objects.equals(numReviews, that.numReviews)
                && Arrays.equals(categories, that.categories)
                && Objects.equals(rating, that.rating)
                && Objects.equals(coordinates, that.coordinates)
                && Arrays.equals(transactions, that.transactions)
                && Objects.equals(price, that.price)
                && Objects.equals(address, that.address)
                && Objects.equals(description, that.description)
                && Objects.equals(phone, that.phone)
                && Objects.equals(displayPhone, that.displayPhone)
                && Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, alias, name, imageUrl, isOpen, url, numReviews, rating, coordinates, price,
                address, description, phone, displayPhone, distance);

        result = 31 * result + Arrays.hashCode(categories);
        result = 31 * result + Arrays.hashCode(transactions);
        return result;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", alias='" + alias + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isOpen=" + isOpen +
                ", url='" + url + '\'' +
                ", numReviews=" + numReviews +
                ", categories=" + Arrays.toString(categories) +
                ", rating=" + rating +
                ", coordinates=" + coordinates +
                ", transactions=" + Arrays.toString(transactions) +
                ", price=" + price +
                ", address=" + address +
                ", description='" + description + '\'' +
                ", phone='" + phone + '\'' +
                ", displayPhone='" + displayPhone + '\'' +
                ", distance=" + distance +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(Integer numReviews) {
        this.numReviews = numReviews;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String[] getTransactions() {
        return transactions;
    }

    public void setTransactions(String[] transactions) {
        this.transactions = transactions;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisplayPhone() {
        return displayPhone;
    }

    public void setDisplayPhone(String displayPhone) {
        this.displayPhone = displayPhone;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
