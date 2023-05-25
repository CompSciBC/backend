package bmg.dto;
import lombok.Data;
import java.util.Map;
import bmg.model.User;
import bmg.model.Property;
import java.time.LocalDateTime;

@Data
public class SurveyData {
    private String reservationId;
    private User guest;
    private Property property;
    private Map<String, Integer> qualityMetrics;
    private LocalDateTime submissionTime;
    private String surveyResponse;
}
