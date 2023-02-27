package bmg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Represents a set of latitude and longitude coordinates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {
    private Double latitude;
    private Double longitude;

    /**
     * Returns a URL encoded string of "latitude,longitude"
     *
     * @return A URL encoded string of "latitude,longitude"
     */
    public String getURLEncoded() {
        return URLEncoder.encode(latitude + "," + longitude, StandardCharsets.UTF_8);
    }
}
