package bmg.model;

import bmg.converter.LocalDateTimeConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a contract agreement between a host and a guest to rent a property
 * for a specified period of time. Multiple guests may share the same reservation;
 * this is represented in the database by multiple entries sharing the same id,
 * (hash key) but with different guest ids (range key). The initial reservation
 * entry will be marked with isPrimary = true, and all others (copies) will be
 * isPrimary = false/null.
 */
@DynamoDBTable(tableName = "bmg_reservation")
@Data
public class Reservation {
    public static final String HOST_ID_CHECK_IN_INDEX = "hostId-checkIn-index";
    public static final String PROPERTY_ID_CHECK_IN_INDEX = "propertyId-checkIn-index";
    public static final String GUEST_ID_CHECK_IN_INDEX = "guestId-checkIn-index";

    // available indexes in the reservation table
    @RequiredArgsConstructor
    @Getter
    public enum Index {
        HOST (HOST_ID_CHECK_IN_INDEX, "hostId"),
        PROPERTY (PROPERTY_ID_CHECK_IN_INDEX, "propertyId"),
        GUEST (GUEST_ID_CHECK_IN_INDEX, "guestId");

        private final String NAME;
        private final String KEY;
    }

    @DynamoDBHashKey(attributeName = "id")
    private String id;

    @DynamoDBRangeKey(attributeName = "guestId")
    @DynamoDBIndexHashKey(attributeName = "guestId", globalSecondaryIndexName = GUEST_ID_CHECK_IN_INDEX)
    private String guestId;

    @DynamoDBIndexHashKey(attributeName = "hostId", globalSecondaryIndexName = HOST_ID_CHECK_IN_INDEX)
    private String hostId;

    @DynamoDBIndexHashKey(attributeName = "propertyId", globalSecondaryIndexName = PROPERTY_ID_CHECK_IN_INDEX)
    private String propertyId;

    @DynamoDBAttribute(attributeName = "numGuests")
    private Integer numGuests;

    @DynamoDBIndexRangeKey(
            attributeName = "checkIn",
            globalSecondaryIndexNames = {
                    HOST_ID_CHECK_IN_INDEX,
                    PROPERTY_ID_CHECK_IN_INDEX,
                    GUEST_ID_CHECK_IN_INDEX})
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime checkIn;

    @DynamoDBAttribute(attributeName = "checkOut")
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime checkOut;

    @DynamoDBAttribute(attributeName = "reasonForStay")
    private String reasonForStay;

    @DynamoDBAttribute(attributeName = "isPrimary")
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
    private Boolean isPrimary;
}
