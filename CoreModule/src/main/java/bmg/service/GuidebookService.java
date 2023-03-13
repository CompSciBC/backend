package bmg.service;

import bmg.dto.Guidebook;
import bmg.repository.GuidebookRepository;
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

    /**
     * Retrieves the guidebook content (JSON file) from S3 for a propertyID
     * @param id propertyID
     * @return Guidebook object
     * @throws IOException
     */
    public Guidebook retrieveGbContentFromS3(String id) throws IOException {
            if (REPO.gbInfoExists(id)) {
                S3Object response = REPO.getOne(id+"/content");
                InputStream objectData = response.getObjectContent();
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(objectData, Guidebook.class);
            }
        return null;
    }

    /**
     * Saves the guidebook image files for a particular property in S3
     * @param id propertyID
     * @param files Multiple image files
     * @return a List of Strings of object keys that have been saved in S3
     * @throws IOException
     */
    public List<String> saveGbImagesToS3(String id, MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        String uniqueObjectKey;
        for (MultipartFile file : files) {
            uniqueObjectKey = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            try {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());
                REPO.saveOne(id+"/images/"+uniqueObjectKey, file.getInputStream(), metadata);
                urls.add(uniqueObjectKey);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading file to S3", e);
            }
        }
        return urls;
    }
    public List<String> retrieveGbImagesFromS3(String id) {
            return REPO.retrieveObjectURLs(id);
    }

    /**
     * Deletes both the /images and /content contained in S3 for any one guidebook
     * @param id propertyID
     */
    public void deleteGuidebook(String id) {
        REPO.deleteGbInfoNImages(id);
    }
}
