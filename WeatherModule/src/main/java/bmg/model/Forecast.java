package bmg.model;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import lombok.Data;

@DynamoDBTable(tableName = "bmg_forecast")
@Data
public class Forecast {
    @DynamoDBHashKey(attributeName = "office")
    private String office;

    @DynamoDBRangeKey(attributeName="gridX_gridY")
    private String gridX_gridY;

    @DynamoDBAttribute
    private String timestamp;

    @DynamoDBAttribute
    private String forecast_content;

}
