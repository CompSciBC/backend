package bmg.service;

import bmg.dto.Guidebook;
import bmg.repository.GuidebookRepository;
import com.amazonaws.SdkClientException;
import lombok.RequiredArgsConstructor;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GuidebookService {

    private final GuidebookRepository REPO;

    public String saveGbContentToS3(String id, Guidebook gb) {
        try {
            byte[] jsonBytes = new ObjectMapper().writeValueAsBytes(gb);
            REPO.saveOne(id+"/content", new ByteArrayInputStream(jsonBytes), null);
            return "Saved JSON file to S3";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error saving JSON file to S3";
        }
    }

    public Guidebook retrieveGbContentFromS3(String id) throws IOException {
            if (REPO.gbInfoExists(id)) {
                S3Object response = REPO.getOne(id+"/content");
                InputStream objectData = response.getObjectContent();
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(objectData, Guidebook.class);
            }
    }

    public List<String> saveGbImagesToS3(String id, MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        String uniqueObjectKey;
        for (MultipartFile file : files) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            uniqueObjectKey = UUID.randomUUID() + "-" + file.getOriginalFilename();
            System.out.println(uniqueObjectKey);
            ///
            // PutObjectRequest request = new PutObjectRequest(bucket,uniqueObjectKey, file.getInputStream(), metadata).withKey(GUIDEBOOK_FOLDER+id+"/images/"+uniqueObjectKey);
            ///
            //System.out.println(request);
            ///
//            S3.putObject(request);
//            String url = S3.getUrl(bucket, uniqueObjectKey).toString();
//            urls.add(url);
            ///







                // below can be deleted
//            S3 = AmazonS3ClientBuilder.standard()
//                    .withRegion(region)
//                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
//                    .build();
//            String key = UUID.randomUUID() + "-" + file.getOriginalFilename();
//            PutObjectRequest putRequest = new PutObjectRequest(bucket, key, file.getInputStream(), null).withKey(GUIDEBOOK_FOLDER+id+"/images");
//            S3.putObject(putRequest);
//            String url = S3.getUrl(bucket, key).toString();
//            urls.add(url);
        }
        return urls;
    }
    public List<String> retrieveGbImagesFromS3(String id) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucket).withPrefix(GUIDEBOOK_FOLDER+id+"/images");
        List<S3ObjectSummary> objectSummaries = S3.listObjects(listObjectsRequest).getObjectSummaries();
        return objectSummaries.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }
    public void deleteGuidebook(String id) {
        S3.deleteObject(bucket, GUIDEBOOK_FOLDER+id);
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, GUIDEBOOK_FOLDER+id);

        try {
            S3.deleteObject(deleteObjectRequest);
            System.out.println("Object deleted successfully.");
        } catch (SdkClientException e) {
            System.err.println("Unable to delete object: " + e.getMessage());
        }
    }
}
