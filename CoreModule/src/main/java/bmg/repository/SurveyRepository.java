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
        return findSurveysByIndex(Survey.Index.RESERVATION, resId);
     }

     /**
      * Finds list of surveys associated with a property
      * @param propId
      * @return List of Survey Objects
      */
     public List<Survey> findSurveysByProperty(String propId) {
        return findSurveysByIndex(Survey.Index.PROPERTY, propId);
     }

     /**
      * Finds list of surveys associated with a host
      * @param hostId
      * @return List of Survey Objects
      */
     public List<Survey> findSurveysByHost(String hostId) {
        return findSurveysByIndex(Survey.Index.HOST, hostId);
     }

     /**
      * Finds list of surveys associated with a guest
      * @param guestId
      * @return List of Survey Objects
      */
      public List<Survey> findSurveysByGuest(String guestId) {
        return findSurveysByIndex(Survey.Index.GUEST, guestId);
     }
}
