package bmg.service;
import bmg.dto.Coordinates;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Arrays;
import java.io.*;
import org.json.JSONObject;
import bmg.DynamoDBConnector;
import bmg.model.Forecast;
import bmg.model.ForecastDTO;
import org.springframework.beans.BeanUtils;
import bmg.service.CoordinatesService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.client.RestTemplate;


@Service
public class NwsWeatherService {

    @Autowired
    private CoordinatesService CS;


    // calls the NWS API to get the forecast for a given grid and 
    // return forecast portion of the JSON response as a JSONArray
    private String getForecastUrl(String address) {
        try {
            Coordinates c = CS.getCoordinates(address);
            RestTemplate restTemplate = new RestTemplate();
            String request = String.format("https://api.weather.gov/points/%s,%s", c.getLatitude().toString(), c.getLongitude().toString());

            // TODO: What happens when thing doesn't return the proper thing
            JSONObject response = new JSONObject(restTemplate.getForObject(request, String.class));
            String forecastUrl = response.getJSONObject("properties").getString("forecast");
            return forecastUrl;
        } catch (Exception e) {
            return e.toString();
        }
    }

    public Object getForecast(String address){
        try {
            String forecastUrl = getForecastUrl(address);
            RestTemplate restTemplate = new RestTemplate();
            JSONObject response = new JSONObject(restTemplate.getForObject(forecastUrl, String.class));

            return response.getJSONObject("properties").getJSONArray("periods").toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

}
