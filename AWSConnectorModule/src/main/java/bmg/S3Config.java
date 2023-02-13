package bmg;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AWSConfig AWS;

    @Bean
    public AmazonS3 s3Client() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(AWS.amazonAWSCredentialsProvider())
                .withRegion(AWS.getRegion())
                .build();
    }
}
