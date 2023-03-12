package bmg.model;

import lombok.Data;

@Data
public class Forecast {
    private String date;
    private boolean isDaytime;
    private String detailedForecast;
    private double temperature;
    private String windDirection;
    private String windSpeed;
    private int preciptationProbability;
}
