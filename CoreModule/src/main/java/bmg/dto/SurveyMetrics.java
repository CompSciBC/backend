package bmg.dto;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SurveyMetrics {
    private String hostId;
    // private ? cumulativeMetrics;
    private List<SurveyData> surveyResponses;
    private Map<String, List<PieChartDataPoint>> pieChartData;
}
