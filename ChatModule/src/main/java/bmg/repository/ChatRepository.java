package bmg.repository;


import bmg.model.MessageDBRecord;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.stereotype.Repository;




@Repository
public class ChatRepository {
    private final DynamoDBMapper dynamoDBMapper;

    ChatRepository(DynamoDBMapper dynamoDBMapper){
        this.dynamoDBMapper = dynamoDBMapper;

    }

    /**
     * Saves the given messageDBRecord into PrivateChat table
     */
    public void saveMessage(MessageDBRecord messageDBRecord) {

       dynamoDBMapper.save(messageDBRecord);
    }

    /**
     * Retrieve message for the given reservationID
     */
    public List<MessageDBRecord> retrieveMessageForGivenReservationId(String reservationId) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":pk", new AttributeValue().withS(reservationId));
        return dynamoDBMapper.query(
                MessageDBRecord.class,
                new DynamoDBQueryExpression <MessageDBRecord>()
                        .withKeyConditionExpression("ReservationID = :pk")
                        .withExpressionAttributeValues(values)
                        .withScanIndexForward(true)
                        .withConsistentRead(false) );
    }


    /**
     * Retrieve message for the given chatId
     */
    public List<MessageDBRecord> retrieveMessageForGivenChatId (String chatId){
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":pk", new AttributeValue().withS(chatId));


        return dynamoDBMapper.query(
                MessageDBRecord.class,
                new DynamoDBQueryExpression <MessageDBRecord>()
                        .withIndexName("chatID-timestamp-index")
                        .withKeyConditionExpression("chatID = :pk")
                        .withExpressionAttributeValues(values)
                        .withScanIndexForward(true)
                        .withConsistentRead(false) );
    }

}
