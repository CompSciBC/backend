package bmg.converter;

import bmg.dto.Address;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts an {@link Address} object
 */
public class AddressConverter implements DynamoDBTypeConverter<Map<String, String>, Address> {

    @Override
    public Map<String, String> convert(Address address) {
        Map<String, String> map = new HashMap<>();
        String line1 = address.getLine1();
        String line2 = address.getLine2();
        String city = address.getCity();
        String stateProvince = address.getStateProvince();
        String postalCode = address.getPostalCode();
        String country = address.getCountry();

        if (line1 != null) map.put(Address.Fields.line1, line1);
        if (line2 != null) map.put(Address.Fields.line2, line2);
        if (city != null) map.put(Address.Fields.city, city);
        if (stateProvince != null) map.put(Address.Fields.stateProvince, stateProvince);
        if (postalCode != null) map.put(Address.Fields.postalCode, postalCode);
        if (country != null) map.put(Address.Fields.country, country);

        return map;
    }

    @Override
    public Address unconvert(Map<String, String> map) {
        Address address = new Address();

        address.setLine1(map.get(Address.Fields.line1));
        address.setLine2(map.get(Address.Fields.line2));
        address.setCity(map.get(Address.Fields.city));
        address.setStateProvince(map.get(Address.Fields.stateProvince));
        address.setPostalCode(map.get(Address.Fields.postalCode));
        address.setCountry(map.get(Address.Fields.country));

        return address;
    }
}
