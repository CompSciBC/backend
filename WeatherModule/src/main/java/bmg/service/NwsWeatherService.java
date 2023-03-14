package bmg.service;
import bmg.dto.Coordinates;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;
import bmg.model.Forecast;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.client.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class NwsWeatherService {

    @Autowired
    private CoordinatesService CS;

    private JSONObject getApiResult(String request) {
        int maxTries = 3;
        int triesCount = 0;
        RestTemplate restTemplate = new RestTemplate();
        while (true) {
            try {
                return new JSONObject(restTemplate.getForObject(request, String.class));
            } catch (HttpServerErrorException e) {
                if (++triesCount == maxTries) {
                    throw e;
                }
            }
        }
    }

    // calls the NWS API to get the forecast for a given grid and 
    // return forecast portion of the JSON response as a JSONArray
    public List<Forecast> getTenDayForecast(String address) throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException {
        Coordinates c = CS.getCoordinates(address);

        JSONObject forecastsEndpoint = getApiResult(String.format(
            "https://api.weather.gov/points/%s,%s", 
            c.getLatitude(), c.getLongitude()
        ));

        JSONObject forecasts = getApiResult(forecastsEndpoint
            .getJSONObject("properties")
            .getString("forecast")
        );

        JSONArray rawForecastResults = forecasts.getJSONObject("properties")
        .getJSONArray("periods");

        List<Forecast> deserializedForecastResults = new ArrayList<>();
        ObjectMapper ObjectMapper = new ObjectMapper();
        for (Object rawResult : rawForecastResults) {
            Forecast forecast = ObjectMapper.readValue(rawResult.toString(), Forecast.class);
            deserializedForecastResults.add(forecast);
        }
        return deserializedForecastResults;
        
    }
}
