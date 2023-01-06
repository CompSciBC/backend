package bmg;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConnector {
    private final DynamoDBMapper dynamoDBMapper;
    private final DynamoDbConfig dynamoDbConfig;

    public DynamoDBConnector(DynamoDBMapper dynamoDBMapper, DynamoDbConfig dynamoDbConfig) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.dynamoDbConfig = dynamoDbConfig;
    }

    // TODO: use generics
    // TODO: add try catch to handle when object doesn't exist
    public <T> Object queryFromDynamoDB(T object, T hashKey, T rangeKey){
        return dynamoDBMapper.load(object.getClass(), hashKey, rangeKey);
    }

    // TODO: add code to check if object was saved
    public void saveToDynamoDB(Object o){
        dynamoDBMapper.save(o);
    }
    
}
