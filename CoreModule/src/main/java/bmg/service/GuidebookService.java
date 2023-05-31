package bmg.service;

import bmg.dto.GuidebookImage;
import bmg.dto.GuidebookImageMetadata;
import bmg.repository.GuidebookRepository;
import lombok.RequiredArgsConstructor;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class GuidebookService {

    private final GuidebookRepository REPO;

    /**
     * Saves a guidebook dto object to S3 for a particular property with parameter passed propertyID and Guidebook gb
     * @param id propertyID
     * @param gb Guidebook JSON file
     * @return
     */
    public String saveGbContentToS3(String id, Object gb) {
        try {
            byte[] jsonBytes = new ObjectMapper().writeValueAsBytes(gb);
            REPO.saveOne(id+"/content", new ByteArrayInputStream(jsonBytes), null, null);
            return "Saved JSON file to S3";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error saving JSON file to S3";
        }
    }

    /**
     * Retrieves the guidebook content (JSON file) from S3 for a propertyID
     * @param id propertyID
     * @return Guidebook object
     * @throws IOException
     */
    public Object retrieveGbContentFromS3(String id) throws IOException {
            if (REPO.gbInfoExists(id)) {
                S3Object response = REPO.getOne(id+"/content");
                InputStream objectData = response.getObjectContent();
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(objectData, Object.class);
            }
        return null;
    }

    /**
     * Saves the guidebook image files for a particular property in S3
     * @param id propertyID
     * @param files Multiple image files
     * @return a List of guidebook images that have been saved in S3
     */
    public List<GuidebookImage> saveGbImagesToS3(String id, MultipartFile[] files, GuidebookImageMetadata[] metadata) {
        List<GuidebookImage> images = new ArrayList<>();
        String uniqueObjectKey;
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            GuidebookImageMetadata meta = metadata[i];
            uniqueObjectKey = UUID.randomUUID().toString() + "-" + meta.getName();
            try {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(file.getContentType());
                objectMetadata.setContentLength(file.getSize());
                objectMetadata.setUserMetadata(Map.of("fileName", meta.getName()));
                List<Tag> tags = Arrays.stream(meta.getTags()).map((tag) -> new Tag(tag, "true")).toList();

                String url = REPO
                        .saveOne(id+"/images/"+uniqueObjectKey, file.getInputStream(), objectMetadata, tags)
                        .toString();

                images.add(GuidebookImage.builder().url(url).metadata(meta).build());

            } catch (IOException e) {
                throw new RuntimeException("Error uploading file to S3", e);
            }
        }
        return images;
    }

    /**
     * Retrieves all images for the identified guidebook
     *
     * @param id A property id
     * @return A list of guidebook images
     */
    public List<GuidebookImage> retrieveGbImagesFromS3(String id) {
        return REPO.retrieveGuidebookImages(id);
    }

    /**
     * Retrieves the url of the featured image for the identified guidebook
     *
     * @param id A property id
     * @return The first image tagged as "Featured", or the last image if no featured images exist
     */
    public String retrieveGbFeaturedImageFromS3(String id) {
        return REPO.retrieveGuidebookFeaturedImage(id);
    }

    /**
     * Deletes both the /images and /content contained in S3 for any one guidebook
     * @param id propertyID
     */
    public void deleteGuidebook(String id) {
        REPO.deleteGbInfoNImages(id);
    }

    /**
     * Deletes the identified guidebook image
     *
     * @param imageUrl A URL identifying a guidebook image
     */
    public void deleteGuidebookImage(String imageUrl) {
        REPO.deleteGuidebookImage(imageUrl);
    }
}
