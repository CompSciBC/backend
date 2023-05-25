package bmg.dto;
import lombok.Data;
import java.util.List;

@Data
public class SurveyMetrics {
    private String hostId;
    // private ? cumulativeMetrics;
    private List<SurveyData> surveyResponses;
    private List<PieChartDataPoint> pieChartData;
}
