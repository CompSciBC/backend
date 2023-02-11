package bmg.model;

import lombok.*;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter

/**
 * Represents a chat...
 */
@DynamoDBTable(tableName = "Chat")
@Data
public class MessageDBRecord {

    //Partition Key
    @DynamoDBHashKey (attributeName = "ReservationID")
    private String reservationId;
    //Sort Key
    @DynamoDBRangeKey (attributeName = "timestamp")
    private Long timestamp;
    //global key
    @DynamoDBIndexHashKey (attributeName = "chatID", globalSecondaryIndexName = "chatID-timestamp-index")
    private String chatId;
    //Attributes
    @DynamoDBAttribute (attributeName = "senderName")
    private String senderName;
    @DynamoDBAttribute (attributeName = "message")
    private String message;

}

