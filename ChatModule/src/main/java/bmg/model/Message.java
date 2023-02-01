package bmg.model;

import lombok.*;
import com.amazonaws.services.dynamodbv2.datamodeling.*;


@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter

/**
 * Represents a private chat between a host and a guest for a given reservationID message
 */
@DynamoDBTable(tableName = "Private")
@Data
public class Message {
    //Partition Key
    @DynamoDBHashKey (attributeName = "reservationID")
    private String reservationId;
    //Sort Key
    @DynamoDBRangeKey (attributeName = "timestamp")
    private String timestamp;
    //Attributes
    @DynamoDBAttribute (attributeName = "senderName")
    private String senderName;
    @DynamoDBAttribute (attributeName = "receiverName")
    private String receiverName;
    @DynamoDBAttribute (attributeName = "message")
    private String message;
    //@DynamoDBAttribute(attributeName = "status")
    //private Status status;

}
