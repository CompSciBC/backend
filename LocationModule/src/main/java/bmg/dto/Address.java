package bmg.dto;

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

    public String getAddressString() {
        return (line1 != null ? line1 : "") + " "
                + (line2 != null ? line2 : "") +  " "
                + (city != null ? city : "") +  " "
                + (stateProvince != null ? stateProvince : "") +  " "
                + (postalCode != null ? postalCode : "") +  " "
                + (country != null ? country : "");
    }
}
