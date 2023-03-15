package bmg;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
// import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DynamoDbConfig {

    private final AWSConfig AWS;

    @Value("${aws.dynamodb.endpoint}")
    private String awsDynamoDBEndPoint;

    // Returns the amazonDB instance using the endpoint as well as credentials
    public AmazonDynamoDB amazonDynamoDB(){
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder
                                .EndpointConfiguration(awsDynamoDBEndPoint, AWS.getRegion()))
                .withCredentials(AWS.amazonAWSCredentialsProvider())
                .build();
    }

    @Bean
    public DynamoDBMapper mapper(){
        return new DynamoDBMapper(amazonDynamoDB());
    }

}
