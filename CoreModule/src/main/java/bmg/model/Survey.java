package bmg.model;
import bmg.converter.LocalDateTimeConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@DynamoDBTable(tableName = "bmg-surveys")
@Data
public class Survey {
    public static final String HOST_ID_INDEX = "hostId-index";
    public static final String PROPERTY_ID_INDEX = "propertyId-index";

    // available indexes in the survey table
    @RequiredArgsConstructor
    @Getter
    public enum Index {
        HOST (HOST_ID_INDEX, "hostId"),
        PROPERTY (PROPERTY_ID_INDEX, "propertyId");

        private final String NAME;
        private final String KEY;
    }

    @DynamoDBHashKey(attributeName = "reservationId")
    private String reservationId;

    @DynamoDBRangeKey(attributeName = "guestId")
    private String guestId;


    @DynamoDBIndexHashKey(attributeName = "hostId", globalSecondaryIndexName = HOST_ID_INDEX)
    private String hostId;

    @DynamoDBIndexHashKey(attributeName = "propertyId", globalSecondaryIndexName = PROPERTY_ID_INDEX)
    private String propertyId;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime submissionTime;

    @DynamoDBAttribute(attributeName = "surveyResponse")
    private String surveyResponse;
}
