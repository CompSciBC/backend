package bmg.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a Guidebook image
 */
@Getter
@Builder
public class GuidebookImage {
    private String url;
    private GuidebookImageMetadata metadata;
}
