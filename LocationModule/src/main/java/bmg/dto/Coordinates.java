package bmg.dto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents a set of latitude and longitude coordinates
 */
public class Coordinates {
    private Double latitude;
    private Double longitude;

    public Coordinates() {
    }

    public Coordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Coordinates that))
            return false;

        return latitude.equals(that.latitude)
                && longitude.equals(that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns a URL encoded string of "latitude,longitude"
     *
     * @return A URL encoded string of "latitude,longitude"
     */
    public String getURLEncoded() {
        return URLEncoder.encode(latitude + "," + longitude, StandardCharsets.UTF_8);
    }
}
