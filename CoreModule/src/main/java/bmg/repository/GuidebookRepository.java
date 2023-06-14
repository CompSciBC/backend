package bmg.repository;

import bmg.dto.Dimensions;
import bmg.dto.GuidebookImage;
import bmg.dto.GuidebookImageMetadata;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
@Log4j2
public class GuidebookRepository {
    private final AmazonS3 S3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.bucket.url}")
    private String bucketUrl;

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
     * @return A URL pointing to the saved S3 object
     */
    public URL saveOne(String key, InputStream data, ObjectMetadata metadata, List<Tag> tags){
        PutObjectRequest request = new PutObjectRequest(bucket, GUIDEBOOK_FOLDER+key, data, metadata);
        request.setTagging(new ObjectTagging(tags));
        S3.putObject(request);
        return generatePresignedURL(GUIDEBOOK_FOLDER+key);
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
     * Retrieves all guidebook images contained within a path in S3
     * This method is dynamic, and items returned depend on how many objects are found at the object key path
     * Utilizes the generatePresignedURL method above
     * @param key object key path
     * @param dimensions The dimensions of the desired image (can be null)
     * @return A list of guidebook images
     */
    public List<GuidebookImage> retrieveGuidebookImages(String key, Dimensions dimensions) {
        List<GuidebookImage> images = new ArrayList<>();

        // Create the ListObjectsV2Request, specifying we want to look at property-guidebooks/PID#######/images
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(GUIDEBOOK_FOLDER+key+"/images");
        ListObjectsV2Result result;

        do {
            result = S3.listObjectsV2(listObjectsV2Request);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) { // For each of the objects in the object summary, generate a URL for that object item in S3
                String objectKey = objectSummary.getKey();

                GuidebookImageMetadata metadata = new GuidebookImageMetadata();
                metadata.setName(getObjectFileName(objectKey));
                metadata.setTags(getObjectTags(objectKey));

                String url = bucketUrl + "/" + objectKey;
                if (dimensions != null)
                    url = url.replace("/images", "/" + dimensions + "/images" );

                images.add(GuidebookImage.builder().url(url).metadata(metadata).build());
            }
            listObjectsV2Request.setContinuationToken(result.getNextContinuationToken()); // AWS has designated when a query returns a large number of results, only a subset is returned.
                                                                                            // We don't know how many images the host uploaded, so we continue to get the next result set for all images
        } while(result.isTruncated());
        return images;
    }

    /**
     * Retrieves a featured image for the identified guidebook
     *
     * @param key A property id
     * @param dimensions The dimensions of the desired image (can be null)
     * @return The url for the first image tagged as "Featured", or the last image if no featured images exist
     */
    public String retrieveGuidebookFeaturedImage(String key, Dimensions dimensions) {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(GUIDEBOOK_FOLDER+key+"/images");
        ListObjectsV2Result result;

        do {
            result = S3.listObjectsV2(request);
            List<S3ObjectSummary> summaries = result.getObjectSummaries();

            for (int i = 0; i < summaries.size(); i++) {
                S3ObjectSummary objectSummary = summaries.get(i);
                String objectKey = objectSummary.getKey();
                String[] tags = getObjectTags(objectKey);

                for (String tag : tags) {

                    // return last image if no featured tags found
                    if (i == summaries.size() - 1  || tag.equalsIgnoreCase("featured")) {
                        String url = bucketUrl + "/" + objectKey;

                        if (dimensions != null)
                            url = url.replace("/images", "/" + dimensions + "/images" );

                        return url;
                    }
                }
            }
            request.setContinuationToken(result.getNextContinuationToken());

        } while(result.isTruncated());

        // this should be unreachable
        return null;
    }

    /**
     * Gets a list of tags associated with the S3 object
     *
     * @param objectKey An S3 object key
     * @return An array of tag key names
     */
    private String[] getObjectTags(String objectKey) {
        return S3
                .getObjectTagging(new GetObjectTaggingRequest(bucket, objectKey))
                .getTagSet()
                .stream()
                .map(Tag::getKey)
                .toArray(String[]::new);
    }

    /**
     * Gets the object fileName metadata for the S3 object
     *
     * @param objectKey An S3 object key
     * @return The fileName metadata string
     */
    private String getObjectFileName(String objectKey) {
        String fileName = S3.getObjectMetadata(bucket, objectKey).getUserMetaDataOf("fileName");

        /*
            all the test data images do not have a fileName metadata so this is a band-aid
            to ensure readable names on the UI. In production, all image objects in S3 will
            have a fileName
        */
        if (fileName == null) {

            // guidebook service prepends a UUID and "-" to the file name
            int uuidLength = UUID.randomUUID().toString().length();
            String pattern = String.format(".*/[^/]{%d}(.+)$", uuidLength + 1);

            Matcher matcher = Pattern
                    .compile(pattern)
                    .matcher(objectKey);

            if (matcher.find())
                fileName = matcher.group(1);
            else
                fileName = objectKey;
        }
        return fileName;
    }

    /**
     * Deletes the identified guidebook image
     *
     * @param imageUrl A URL identifying a guidebook image
     */
    public void deleteGuidebookImage(String imageUrl) {
        Matcher matcher = Pattern.compile("("+GUIDEBOOK_FOLDER+".*?)\\?").matcher(imageUrl);

        if (matcher.find()) {
            S3.deleteObject(bucket, matcher.group(1));
        } else {
            log.error("No photo found at {}", imageUrl);
            throw new NoSuchElementException("No photo found at " + imageUrl);
        }
    }
}