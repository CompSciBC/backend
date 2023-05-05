package bmg.converter;

import bmg.dto.Address;
import bmg.model.Property;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/**
 * Converts a {@link Property} object
 */
@Log4j2
public class PropertyConverter implements DynamoDBTypeConverter<Map<String, AttributeValue>, Property> {

    @Override
    public Map<String, AttributeValue> convert(Property property) {
        ObjectMapper mapper = new ObjectMapper();
        Item item = new Item();

        String id = property.getId();
        String hostId = property.getHostId();
        String name = property.getName();
        Address address = property.getAddress();

        if (id != null)
            item.withString(Property.Fields.id, id);

        if (hostId != null)
            item.withString(Property.Fields.hostId, hostId);

        if (name != null)
            item.withString(Property.Fields.name, name);

        if (address != null)
            item.withMap(Property.Fields.address,
                    mapper.convertValue(address, new TypeReference<Map<String, Object>>() {}));

        return ItemUtils.toAttributeValues(item);
    }

    @Override
    public Property unconvert(Map<String, AttributeValue> map) {
        Property property;

        ObjectMapper mapper = new ObjectMapper();
        String item = ItemUtils.toItem(map).toJSON();

        try {
            JsonNode json = mapper.readTree(item);
            property = mapper.convertValue(json, Property.class);

        } catch (JsonProcessingException e) {
            log.error("Error un-converting map={} to Property", map);
            throw new RuntimeException(e);
        }
        return property;
    }
}
