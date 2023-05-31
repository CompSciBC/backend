package bmg.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
/**
 * Represents an inbox...
 */

@DynamoDBTable(tableName = "Inbox")
@Data
public class InboxDBRecord {
    //Partition Key
    @DynamoDBHashKey(attributeName = "userId")
    private String userId;
    //Sort Key
    @DynamoDBRangeKey (attributeName = "timestamp")
    private Long timestamp;



    //Attributes
    @DynamoDBAttribute(attributeName = "senderName")
    private String senderName;
    @DynamoDBAttribute (attributeName = "message")
    private String message;
    @DynamoDBAttribute (attributeName = "chatId")
    private String chatId;
    @DynamoDBAttribute (attributeName = "reservationId")
    private String reservationId;
}
