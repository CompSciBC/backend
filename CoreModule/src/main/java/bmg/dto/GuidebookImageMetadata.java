package bmg.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Metadata associated with a guidebook image file
 */
@Getter
@Setter
public class GuidebookImageMetadata {
    private String name;
    private String[] tags;
}
