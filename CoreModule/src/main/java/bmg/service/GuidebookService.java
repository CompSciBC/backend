package bmg.service;

import bmg.dto.Guidebook;
import bmg.model.Property;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.ByteArrayInputStream;
import java.io.File;

@Repository
@RequiredArgsConstructor
public class GuidebookService {

    private final AmazonS3 S3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    private static final String GUIDEBOOK_FOLDER = "guidebookJSON/";

//    public PutObjectResult saveGuidebookJsonInfo(String id, String jsonPayload) {
//        byte[] jsonBytes = jsonPayload.getBytes();
//        PutObjectResult guidebookResult = S3.putObject(bucket, GUIDEBOOK_FOLDER+id, new ByteArrayInputStream(jsonBytes), null);
////        return "Saved JSON to S3";
////        S3.putObject(bucket, GUIDEBOOK_FOLDER+id, guidebookInfo);
//        return guidebookResult;
//    }


    public String saveToS3(Guidebook gb) {
        try {
            byte[] jsonBytes = new ObjectMapper().writeValueAsBytes(gb);
            S3.putObject(new PutObjectRequest(bucket, GUIDEBOOK_FOLDER, new ByteArrayInputStream(jsonBytes), null));
            return "Saved JSON file to S3";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error saving JSON file to S3";
        }
    }

//    public void retrieveGuidebookJsonInfo(String id) {
//        S3.getObject("/", "");
//        S3.getObject(GetObjectRequest);
//    }

//    public void saveGuidebookImages(String id, File guidebookInfo) {
//        S3.putObject(bucket, GUIDEBOOK_FOLDER+id+"/images", guidebookInfo);
//    }

    public void deleteGuidebook(String id) {
        S3.deleteObject(bucket, GUIDEBOOK_FOLDER+id);
    }

}
