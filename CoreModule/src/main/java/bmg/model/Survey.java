package bmg.model;
import bmg.converter.LocalDateTimeConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@DynamoDBTable(tableName = "bmg_survey")
@Data
public class Survey {
    public static final String HOST_ID_INDEX = "hostId-index";
    public static final String RESERVATION_ID_INDEX = "reservationId-index";
    public static final String GUEST_ID_INDEX = "guestId-index";
    public static final String PROPERTY_ID_INDEX = "propertyId-index";

    // available indexes in the survey table
    @RequiredArgsConstructor
    @Getter
    public enum Index {
        HOST (HOST_ID_INDEX, "hostId"),
        RESERVATION (RESERVATION_ID_INDEX, "reservationId"),
        GUEST (GUEST_ID_INDEX, "guestId"),
        PROPERTY (PROPERTY_ID_INDEX, "propertyId");

        private final String NAME;
        private final String KEY;
    }

    @DynamoDBHashKey(attributeName = "id")
    private String id;

    // @DynamoDBRangeKey(attributeName = "guestId")
    @DynamoDBIndexHashKey(attributeName = "guestId", globalSecondaryIndexName = GUEST_ID_INDEX)
    private String guestId;

    @DynamoDBIndexHashKey(attributeName = "hostId", globalSecondaryIndexName = HOST_ID_INDEX)
    private String hostId;

    @DynamoDBIndexHashKey(attributeName = "reservationId", globalSecondaryIndexName = RESERVATION_ID_INDEX)
    private String reservationId;

    @DynamoDBIndexHashKey(attributeName = "propertyId", globalSecondaryIndexName = PROPERTY_ID_INDEX)
    private String propertyId;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime submissionTime;

    @DynamoDBAttribute(attributeName = "surveyResponse")
    private String surveyResponse;
}
