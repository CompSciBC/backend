package bmg.converter;

import bmg.dto.GuidebookImageMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts a JSON string array into a {@link GuidebookImageMetadata}
 */
@Component
public class GuidebookImageMetadataConverter implements Converter<String, GuidebookImageMetadata[]> {
    @Override
    public GuidebookImageMetadata[] convert(@NotNull String source) {
        try {
            return new ObjectMapper().readValue(source, GuidebookImageMetadata[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
