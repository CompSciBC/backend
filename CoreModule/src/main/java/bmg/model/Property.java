package bmg.model;

import bmg.converter.AddressConverter;
import bmg.dto.Address;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * Represents a real estate property owned and/or operated by a host for rental to a guest
 */
@DynamoDBTable(tableName = "bmg_property")
@Data
@FieldNameConstants
public class Property {
    public static final String HOST_ID_NAME_INDEX = "hostId-name-index";

    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBIndexHashKey(attributeName = "hostId", globalSecondaryIndexName = HOST_ID_NAME_INDEX)
    private String hostId;

    @DynamoDBIndexRangeKey(attributeName = "name", globalSecondaryIndexName = HOST_ID_NAME_INDEX)
    private String name;

    @DynamoDBAttribute(attributeName = "address")
    @DynamoDBTypeConverted(converter = AddressConverter.class)
    private Address address;
}
