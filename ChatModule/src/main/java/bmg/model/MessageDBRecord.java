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
public class Message {

    //Partition Key
    @DynamoDBHashKey (attributeName = "reservationID")
    private String reservationId;
    //Sort Key
    @DynamoDBRangeKey (attributeName = "timestamp")
    private String timestamp;
    //global key
    @DynamoDBIndexHashKey (attributeName = "chatId", globalSecondaryIndexName = "chatId-index")
    private String chatId;
    //Attributes
    @DynamoDBAttribute (attributeName = "senderName")
    private String senderName;
    @DynamoDBAttribute (attributeName = "message")
    private String message;


}
