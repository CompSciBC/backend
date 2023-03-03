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

@Service
public class NwsWeatherService {

    
    private final CoordinatesService CS = new CoordinatesService();
    

    // // Method to make an HTTP API call and return the response as a JSONObject
    // private JSONObject getAPIResponse(URL url) throws IOException{
        
    //     URLConnection conn = url.openConnection();
    //     BufferedReader in = new BufferedReader(
    //                             new InputStreamReader(
    //                             conn.getInputStream()));
    //     String currentLine;
    //     StringBuilder jsonString = new StringBuilder();


    //     while ((currentLine = in.readLine()) != null){
    //         jsonString.append(currentLine);
    //     }
    //     in.close();
    //     JSONObject jsonObj = new JSONObject(jsonString.toString());
    //     return jsonObj;
    // }

    // private List<String> getGridEndPoint(double latitude, double longitude) throws IOException{
    //     URL getEndpointURL = new URL(
    //         String.format("https://api.weather.gov/points/%f,%f",latitude, longitude));
    //     JSONObject response = getAPIResponse(getEndpointURL);
    //     String office = response.getJSONObject("properties").get("gridId").toString();
    //     String gridX = response.getJSONObject("properties").get("gridX").toString();
    //     String gridY = response.getJSONObject("properties").get("gridY").toString();
    //     return Arrays.asList(office, gridX, gridY);
    // }

    // // calls the NWS API to get the forecast for a given grid and 
    // // return forecast portion of the JSON response as a JSONArray
    // private JSONObject getForecast1(List<String> endpoints) throws IOException{
    //     String office = endpoints.get(0);
    //     String gridX = endpoints.get(1);
    //     String gridY = endpoints.get(2);
    //     URL getForecastURL = new URL(
    //         String.format("https://api.weather.gov/gridpoints/%s/%s,%s/forecast",office, gridX, gridY));
    //     JSONObject response = getAPIResponse(getForecastURL);
    //     // return response.getJSONObject("properties").getJSONArray("periods");
    //     return response.getJSONObject("properties");
    // }

    public Object getForecast(String address){
        try {
            return CS.getCoordinates(address);
        } catch (Exception e) {
            return e.toString();
        }
        
        
    }

}
