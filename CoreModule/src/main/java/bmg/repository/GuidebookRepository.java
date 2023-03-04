package bmg.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class GuidebookRepository {
    private final AmazonS3 S3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    private static final String GUIDEBOOK_FOLDER = "property-guidebooks/";

    public boolean gbInfoExists(String id) {
        return S3.doesObjectExist(bucket, id);
    }

    public void saveOne(String key, InputStream data, ObjectMetadata metadata){
        S3.putObject(new PutObjectRequest(bucket, GUIDEBOOK_FOLDER+key, data, metadata));
    }

    public S3Object getOne(String key){
        return S3.getObject(new GetObjectRequest(bucket, GUIDEBOOK_FOLDER+key));
    }
}
