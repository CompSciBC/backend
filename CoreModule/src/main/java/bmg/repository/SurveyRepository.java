package bmg.repository;

import bmg.converter.LocalDateTimeConverter;
import bmg.model.Survey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
     * Finds a survey by resId
     */

     public List<Survey> findSurveyByReservation(String resId) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":r_id", new AttributeValue().withS(resId));
        return MAPPER.query(
                Survey.class,
                new DynamoDBQueryExpression<Survey>()
                        .withIndexName("reservationId-index")
                        .withKeyConditionExpression("reservationId = :r_id")
                        .withExpressionAttributeValues(values)
                        .withConsistentRead(false));
     }

     /**
     * Finds a survey by resId
     */

     public List<Survey> findSurveysByProperty(String propId) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":p_id", new AttributeValue().withS(propId));
        return MAPPER.query(
                Survey.class,
                new DynamoDBQueryExpression<Survey>()
                        .withIndexName("propertyId-index")
                        .withKeyConditionExpression("propertyId = :p_id")
                        .withExpressionAttributeValues(values)
                        .withConsistentRead(false));
     }
}
