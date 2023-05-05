package bmg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * Represents the physical address of a location
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class Address {
    private String line1;
    private String line2;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;

    @JsonIgnore
    public String getAddressString() {
        String[] parts = new String[]{line1, line2, city, stateProvince, postalCode, country};
        StringBuilder address = new StringBuilder();

        for (String part : parts) {
            if (part != null)
                address.append(address.isEmpty() ? "" : " ").append(part.trim());
        }
        return address.toString();
    }
}
