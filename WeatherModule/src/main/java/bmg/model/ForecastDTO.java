package bmg.model;

import lombok.Data;

@Data
public class ForecastDTO {
    private String office;
    private String gridX_gridY;
    private String timestamp;
    private String forecast_content;

    public void setDefaultValues(){
        this.office = "SEW";
        this.gridX_gridY = "124, 67";
        this.timestamp = "2023-01-04T19:24:42.727245Z";
        this.forecast_content = "some_forecast";
    }
}
