package bmg.model;

import java.util.Date;
import java.util.Map;

import lombok.Data;

@Data
public class Forecast {
    private String detailedForecast;
    private Map<String, Object> dewpoint;
    private String temperatureTrend;
    private String shortForecast;
    private String icon;
    private int number;
    private String temperatureUnit;
    private Map<String, Object> probabilityOfPrecipitation;
    private String name;
    private int temperature;
    private Map<String, Object> relativeHumidity;
    private Date startTime;
    private String isDaytime;
    private Date endTime;
    private String windDirection;
    private String windSpeed;
}
