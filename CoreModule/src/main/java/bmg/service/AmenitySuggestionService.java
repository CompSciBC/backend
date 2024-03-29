package bmg.service;

import bmg.dto.AmenitySuggestion;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Generates amenity suggestions
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AmenitySuggestionService {

    private final AmazonRekognition REK;
    private final GuidebookService G_SVC;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.rekognition.labelInclusionFilters}")
    private List<String> labelInclusionFilters;

    /**
     * Gets amenity suggestions from the given image
     *
     * @param imageUrl An image url
     * @return A set of amenity suggestions
     */
    public Set<AmenitySuggestion> getSuggestions(String imageUrl) {
        Set<AmenitySuggestion> suggestions = new HashSet<>();

        S3Object object = new S3Object().withName(G_SVC.extractObjectKey(imageUrl)).withBucket(bucket);
        Image image = new Image().withS3Object(object);

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(image)
                .withMaxLabels(20)
                .withMinConfidence(75F);

        if (labelInclusionFilters.size() > 0) {
            GeneralLabelsSettings generalLabelsSettings = new GeneralLabelsSettings()
                    .withLabelInclusionFilters(labelInclusionFilters);

            DetectLabelsSettings detectLabelsSettings = new DetectLabelsSettings()
                    .withGeneralLabels(generalLabelsSettings);

            request.setSettings(detectLabelsSettings);
        }

        try {
            DetectLabelsResult result = REK.detectLabels(request);

            for (Label label : result.getLabels()) {
                AmenitySuggestion suggestion = AmenitySuggestion
                        .builder()
                        .name(label.getName())
                        .confidence(label.getConfidence())
                        .parents(label.getParents().stream().map(Parent::getName).toArray(String[]::new))
                        .boxes(label.getInstances().stream().map(Instance::getBoundingBox).toArray(BoundingBox[]::new))
                        .build();
                suggestions.add(suggestion);
            }
        } catch (AmazonRekognitionException e) {
            log.error(e);
            throw new AmazonRekognitionException(e.getMessage());
        }
        return suggestions;
    }
}
