package bmg.dto;

import java.util.Objects;

/**
 * Represents the physical address of a location
 */
public class Address {
    private String line1;
    private String line2;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;

    public Address() {
    }

    public Address(String line1, String line2, String city,
                   String stateProvince, String postalCode, String country) {

        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Address address))
            return false;

        return Objects.equals(line1, address.line1)
                && Objects.equals(line2, address.line2)
                && Objects.equals(city, address.city)
                && Objects.equals(stateProvince, address.stateProvince)
                && Objects.equals(postalCode, address.postalCode)
                && Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line1, line2, city, stateProvince, postalCode, country);
    }

    @Override
    public String toString() {
        return "Address{" +
                "line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", city='" + city + '\'' +
                ", stateProvince='" + stateProvince + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddressString() {
        return (line1 != null ? line1 : "") + " "
                + (line2 != null ? line2 : "") +  " "
                + (city != null ? city : "") +  " "
                + (stateProvince != null ? stateProvince : "") +  " "
                + (postalCode != null ? postalCode : "") +  " "
                + (country != null ? country : "");
    }
}
