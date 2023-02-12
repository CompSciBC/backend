package bmg.repository;

import bmg.converter.LocalDateTimeConverter;
import bmg.model.Survey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
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
}
