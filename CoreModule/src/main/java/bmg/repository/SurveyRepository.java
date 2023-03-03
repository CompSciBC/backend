package bmg.repository;

import bmg.model.Survey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides CRUD operations for {@link Survey} objects
 */
@Repository
@RequiredArgsConstructor
public class SurveyRepository {
    private final DynamoDBMapper MAPPER;

    /**
     * Saves a guest's survey response
     *
     * @param survey A guest's survey response
     */
    public void saveOne(Survey survey) {
        MAPPER.save(survey);
    }

    /**
     * Finds list of surveys by the index
     * @param index
     * @param id
     * @return List of Survey Objects
     */
    public List<Survey> findSurveysByIndex(Survey.Index index, String id){
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":id", new AttributeValue().withS(id));
        return MAPPER.query(
                Survey.class,
                new DynamoDBQueryExpression<Survey>()
                        .withIndexName(index.getNAME())
                        .withKeyConditionExpression(index.getKEY() + " = :id")
                        .withExpressionAttributeValues(values)
                        .withConsistentRead(false));
    }

    /**
     * Finds list of surveys belonging to a reservation
     * @param resId
     * @return List of Survey Objects
     */
     public List<Survey> findSurveysByReservation(String resId) {
      Map<String, AttributeValue> values = new HashMap<>();
      values.put(":id", new AttributeValue().withS(resId));
      return MAPPER.query(
         Survey.class,
         new DynamoDBQueryExpression<Survey>()
                 .withKeyConditionExpression("reservationId = :id")
                 .withExpressionAttributeValues(values)
                 .withConsistentRead(false));
     }

     /**
      * Find the survey submitted for a reservation by a guest
      * @param propId
      * @return List of Survey Objects
      */
     public Survey findSurveyByReservationAndGuest(String resId, String guestId) {
        Survey s = new Survey();
        s.setReservationId(resId);
        s.setGuestId(guestId);
        return MAPPER.load(s);
     }
}
