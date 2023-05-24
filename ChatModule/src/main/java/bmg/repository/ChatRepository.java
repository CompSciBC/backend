package bmg.repository;


import bmg.model.InboxDBRecord;
import bmg.model.MessageDBRecord;
import bmg.model.Reservation;
import bmg.model.User;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;



@Repository
@RequiredArgsConstructor
public class ChatRepository {
    private final DynamoDBMapper dynamoDBMapper;
    private final ReservationRepository reservationRepository;

    /**
     * Saves the given messageDBRecord into Chat table
     */
    public void saveMessageChat(MessageDBRecord messageDBRecord) {
        Timer timer = Timer.builder("chat.db.save-message.latency").register(Metrics.globalRegistry);
        timer.record(() -> dynamoDBMapper.save(messageDBRecord));

    }

    /**
            * Saves the given messageDBRecord into Inbox table
     */
    public void saveMessageInbox(InboxDBRecord inboxDBRecord) {
        // Timer timer = Timer.builder("inbox.db.save-message.latency").register(Metrics.globalRegistry);
       dynamoDBMapper.save(inboxDBRecord);

    }

    /**
     * Retrieve message for the given reservationID
     */
    public List<MessageDBRecord> retrieveMessageForGivenReservationId(String reservationId) {
        //define a timer
        Timer timer = Timer.builder("chat.db.retrieve-message-reservationId.latency").register(Metrics.globalRegistry);
        //timer starts to count how long the function retrieves data from a database
        Timer.Sample sample = Timer.start(Metrics.globalRegistry);

        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":pk", new AttributeValue().withS(reservationId));
        List<MessageDBRecord> result = new ArrayList<>();

        result = dynamoDBMapper.query(
                MessageDBRecord.class,
                new DynamoDBQueryExpression <MessageDBRecord>()
                        .withKeyConditionExpression("ReservationID = :pk")
                        .withExpressionAttributeValues(values)
                        .withScanIndexForward(true)
                        .withConsistentRead(false) );
        //stops counting
        sample.stop(timer);
        return result;
    }


    /**
     * Retrieve message for the given chatId
     */
    public List<MessageDBRecord> retrieveMessageForGivenChatId (String chatId){
        //define a timer
        Timer timer = Timer.builder("chat.db.retrieve-message-reservationChatId.latency").register(Metrics.globalRegistry);
        //timer starts to count how long the function retrieves data from a database
        Timer.Sample sample = Timer.start(Metrics.globalRegistry);
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":pk", new AttributeValue().withS(chatId));

        List<MessageDBRecord> result = new ArrayList<>();

        result = dynamoDBMapper.query(
                MessageDBRecord.class,
                new DynamoDBQueryExpression <MessageDBRecord>()
                        .withIndexName("chatID-timestamp-index")
                        .withKeyConditionExpression("chatID = :pk")
                        .withExpressionAttributeValues(values)
                        .withScanIndexForward(true)
                        .withConsistentRead(false) );

        //stops counting
        sample.stop(timer);

        return result;
    }

    /**
     * Retrieve the latest message for the reservation ID and sorted by time
     */
    public List<MessageDBRecord> retrieveLatestMessageForGivenReservation(String reservationId) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":pk", new AttributeValue().withS(reservationId));

        return dynamoDBMapper.query(
                MessageDBRecord.class,
                new DynamoDBQueryExpression <MessageDBRecord>()
                        .withKeyConditionExpression("ReservationID = :pk")
                        .withExpressionAttributeValues(values)
                        .withScanIndexForward(true)
                        .withConsistentRead(false));

    }

    /**
     * Retrieve message for the given userID in Inbox table
     */
    public List<InboxDBRecord> retrieveMessageForGivenUserId(String userId) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":pk", new AttributeValue().withS(userId));
        List<InboxDBRecord> result = new ArrayList<>();

        result = dynamoDBMapper.query(
                InboxDBRecord.class,
                new DynamoDBQueryExpression <InboxDBRecord>()
                        .withKeyConditionExpression("userId = :pk")
                        .withExpressionAttributeValues(values)
                        .withScanIndexForward(true)
                        .withConsistentRead(false));

        return result;
    }

    /**
     * Retrieve a userId by a username from the table User
     */
    public User findUsersByUserName(String username) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":pk", new AttributeValue().withS(username));
        User result = new User();
        return dynamoDBMapper.query(
                User.class,
                new DynamoDBQueryExpression<User>()
                        .withIndexName("username-index")
                        .withKeyConditionExpression("username = :pk")
                        .withExpressionAttributeValues(values)
                        .withScanIndexForward(true)
                        .withConsistentRead(false))
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieve a list of users by a given reservationId
     */
    public List<Reservation> findListOfUsersByReservationId(String reservationId) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":pk", new AttributeValue().withS(reservationId));
        return dynamoDBMapper.query(
                Reservation.class,
                new DynamoDBQueryExpression<Reservation>()
                        .withKeyConditionExpression("id = :pk")
                        .withExpressionAttributeValues(values)
                        .withScanIndexForward(true)
                        .withConsistentRead(false));
    }







}
