package bmg.repository;


import bmg.model.Message;
import bmg.model.Status;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.stereotype.Repository;




@Repository
public class PrivateChatRepository {
    private final DynamoDBMapper dynamoDBMapper;

    PrivateChatRepository (DynamoDBMapper dynamoDBMapper){
        this.dynamoDBMapper = dynamoDBMapper;

    }

    /**
     * Saves the given message into PrivateChat table
     */
    public void saveMessage(Message message) {

       dynamoDBMapper.save(message);
    }

    /**
     * Retrieve message for the given reservationID
     */
    public List<Message> retrieveMessageForGivenReservationId(String reservationId) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":pk", new AttributeValue().withS(reservationId));


        return dynamoDBMapper.query(
                Message.class,
                new DynamoDBQueryExpression <Message>()
                        .withKeyConditionExpression("reservationID = :pk")
                        .withExpressionAttributeValues(values)
                        .withConsistentRead(false)
        );
    }

    public List<Message> loadMessage (String reservationId){
        List<Message> loadMessage = new ArrayList<>();
        Message message = new Message();
        message.setReservationId(reservationId);
        loadMessage.add(dynamoDBMapper.load(message));
        return loadMessage;
    }

}
