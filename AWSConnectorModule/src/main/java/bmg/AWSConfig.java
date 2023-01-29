package bmg;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AWSConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.access.key}")
    private String accessKey;

    @Value("${aws.access.secret-key}")
    private String secretKey;

    public AWSCredentials amazonAWSCredentials(){
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    public AWSCredentialsProvider amazonAWSCredentialsProvider(){
        return new AWSStaticCredentialsProvider(amazonAWSCredentials());
    }
}
