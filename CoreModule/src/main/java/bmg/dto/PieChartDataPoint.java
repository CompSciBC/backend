package bmg.dto;
import java.util.Map;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PieChartDataPoint {
    private String name;
    private int count;
    private Map<Integer, Integer> ratingFrequencyMap;
}
