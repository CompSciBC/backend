package bmg.repository;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GuidebookRepository {
    private final AmazonS3 S3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    private static final String GUIDEBOOK_FOLDER = "property-guidebooks/";

    /**
     * Returns a boolean value if guidebook content exists for such an object key
     * @param propID the object key of the item we will examine (typically property id)
     * @return a boolean value true or false
     */
    public boolean gbInfoExists(String propID) {
        return S3.doesObjectExist(bucket, GUIDEBOOK_FOLDER+propID+"/content");
    }

    /**
     * Saves an object into a specified folder within S3
     * @param key the objectkey or folder structure we designate Ex: "/property-guidebooks/<object-key-path->"
     * @param data the item we are storing (JSON obj. in this case, or gb images)
     * @param metadata null, can contain extra data about object if we choose
     */
    public void saveOne(String key, InputStream data, ObjectMetadata metadata){
        S3.putObject(new PutObjectRequest(bucket, GUIDEBOOK_FOLDER+key, data, metadata));
    }

    /**
     * Retrieves an object stored in S3
     * @param key the objectkey or folder structure we designate
     * @return the object at the specified path provided by bucket and objectkey. Should check if an object exists at <path>, before calling method.
     */
    public S3Object getOne(String key){
        return S3.getObject(new GetObjectRequest(bucket, GUIDEBOOK_FOLDER+key));
    }

    /**
     * Deletes both the /content and /images at the specified path
     * @param key the objectkey, stored as the property id.
     */
    public void deleteGbInfoNImages(String key) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, GUIDEBOOK_FOLDER+key);
        try {
            S3.deleteObject(deleteObjectRequest);
            System.out.println("Object deleted successfully.");
        } catch (SdkClientException e) {
            System.err.println("Unable to delete object: " + e.getMessage());
        }
    }

    /**
     * Generates a URL for a specified object in S3
     * @param key the object key, or path in the S3 bucket
     * @return a URL to access the content (image) stored at the object key path
     */
    public URL generatePresignedURL(String key) {
        // Set expiration, set to 7 days (MAX)
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60 * 24 * 7;
        expiration.setTime(expTimeMillis);

        // Make URL request
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        // Return the URL
        return S3.generatePresignedUrl(generatePresignedUrlRequest);
    }

    /**
     * Retrieves a List of URLs of all objects contained within a path in S3
     * This method is dynamic, and items returned depend on how many objects are found at the object key path
     * Utilizes the generatePresignedURL method above
     * @param key object key path
     * @return a List of URLs as Strings
     */
    public List<String> retrieveObjectURLs(String key) {
        List<String> objURLs = new ArrayList<>();

        // Create the ListObjectsV2Request, specifying we want to look at property-guidebooks/PID#######/images
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(GUIDEBOOK_FOLDER+key+"/images");
        ListObjectsV2Result result;

        do {
            result = S3.listObjectsV2(listObjectsV2Request);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) { // For each of the objects in the object summary, generate a URL for that object item in S3
                String objectKey = objectSummary.getKey();
                objURLs.add(generatePresignedURL(objectKey).toString()); // generate the URL, format it to String, and add to our ArrayList of URLs
            }
            listObjectsV2Request.setContinuationToken(result.getNextContinuationToken()); // AWS has designated when a query returns a large number of results, only a subset is returned.
                                                                                            // We don't know how many images the host uploaded, so we continue to get the next result set for all images
        } while(result.isTruncated());
        return objURLs;
    }
}