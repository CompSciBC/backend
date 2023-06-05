package bmg.dto;

import com.amazonaws.services.rekognition.model.BoundingBox;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

/**
 * An amenity suggestion based on image recognition
 */
@Getter
@Builder
public class AmenitySuggestion {
    private String name;
    private Float confidence;
    private String[] parents;
    private BoundingBox[] boxes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AmenitySuggestion that = (AmenitySuggestion) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
