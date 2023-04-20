package bmg.repository;

import bmg.model.User;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides CRUD operations for {@link User} objects
 */
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final DynamoDBMapper MAPPER;

    /**
     * Finds list of users by the index
     * @param index
     * @param id
     * @return List of User Objects
     */
    public List<User> findUsersByIndex(User.Index index, String id){
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":id", new AttributeValue().withS(id));
        return MAPPER.query(
                User.class,
                new DynamoDBQueryExpression<User>()
                        .withIndexName(index.getNAME())
                        .withKeyConditionExpression(index.getKEY() + " = :id")
                        .withExpressionAttributeValues(values)
                        .withConsistentRead(false));
    }

    /**
     * Finds users by userId
     * @param userId
     * @return List of User Objects
     */
     public List<User> findUsersByUserId(String userId) {
      Map<String, AttributeValue> values = new HashMap<>();
      values.put(":id", new AttributeValue().withS(userId));
      return MAPPER.query(
         User.class,
         new DynamoDBQueryExpression<User>()
                 .withKeyConditionExpression("userID = :id")
                 .withExpressionAttributeValues(values)
                 .withConsistentRead(false));
     }




}
