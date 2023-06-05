package bmg;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RekognitionConfig {

    private final AWSConfig AWS;

    @Bean
    public AmazonRekognition rekognitionClient() {
        return AmazonRekognitionClientBuilder
                .standard()
                .withCredentials(AWS.amazonAWSCredentialsProvider())
                .withRegion(AWS.getRegion())
                .build();
    }
}
