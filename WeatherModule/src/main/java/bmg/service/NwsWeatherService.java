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

@Service
public class NwsWeatherService {

    private final DynamoDBConnector dynamoDBConnector;

    Coordinates latLong = new Coordinates(47.6062, -122.3321);
    final long ForecastPersistTime = 2; // minute
    

    public NwsWeatherService(DynamoDBConnector dynamoDBConnector) {
        this.dynamoDBConnector = dynamoDBConnector;
    }

    // TODO: create and move this method to a general WeatherService class
    public Forecast saveForecastToDynamoDB(Forecast forecast) {
        dynamoDBConnector.saveToDynamoDB(forecast);
        return forecast;
    }

    public Forecast queryForecastFromDynamoDB(String officeHashKey, String gridX_gridY_RangeKey){
        Object response = dynamoDBConnector.queryFromDynamoDB(new Forecast(), officeHashKey, gridX_gridY_RangeKey);
        return (Forecast)response;
    }

    public Forecast saveAndRetrieveObjectFromDynamoDBDemo(){
        // Step 1: Create Data object
        Forecast randomForecast = new Forecast();
        randomForecast.setOffice("HDO");
        randomForecast.setGridX_gridY("12_20");
        randomForecast.setForecast_content("some content3");
        randomForecast.setTimestamp(Instant.now().toString());

        // Step 2. Save object to dynamoDB using the Database Module
        dynamoDBConnector.saveToDynamoDB(randomForecast);

        // Step 3: Query from dynamoDB
        Object response = dynamoDBConnector.queryFromDynamoDB(new Forecast(), randomForecast.getOffice(), randomForecast.getGridX_gridY());
        return (Forecast)response;

    }

    // Method to make an HTTP API call and return the response as a JSONObject
    private JSONObject getAPIResponse(URL url) throws IOException{
        
        URLConnection conn = url.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                conn.getInputStream()));
        String currentLine;
        StringBuilder jsonString = new StringBuilder();


        while ((currentLine = in.readLine()) != null){
            jsonString.append(currentLine);
        }
        in.close();
        JSONObject jsonObj = new JSONObject(jsonString.toString());
        return jsonObj;
    }

    private List<String> getGridEndPoint(double latitude, double longitude) throws IOException{
        URL getEndpointURL = new URL(
            String.format("https://api.weather.gov/points/%f,%f",latitude, longitude));
        JSONObject response = getAPIResponse(getEndpointURL);
        String office = response.getJSONObject("properties").get("gridId").toString();
        String gridX = response.getJSONObject("properties").get("gridX").toString();
        String gridY = response.getJSONObject("properties").get("gridY").toString();
        return Arrays.asList(office, gridX, gridY);
    }

    // calls the NWS API to get the forecast for a given grid and 
    // return forecast portion of the JSON response as a JSONArray
    private JSONObject getForecast(List<String> endpoints) throws IOException{
        String office = endpoints.get(0);
        String gridX = endpoints.get(1);
        String gridY = endpoints.get(2);
        URL getForecastURL = new URL(
            String.format("https://api.weather.gov/gridpoints/%s/%s,%s/forecast",office, gridX, gridY));
        JSONObject response = getAPIResponse(getForecastURL);
        // return response.getJSONObject("properties").getJSONArray("periods");
        return response.getJSONObject("properties");
    }


    public String returnWeatherReport() throws IOException{
        Double latitude = latLong.getLatitude();
        Double longitude = latLong.getLongitude();
        List<String> gridInfo = getGridEndPoint(latitude, longitude); // ex: [SEW, 124, 67]

        // get current timestamp
        Instant currentTime = Instant.now();    // ex: 2023-01-04T19:24:42.727245Z

        // check dynamodb for saved forecast
        try {
            Forecast savedForecast= queryForecastFromDynamoDB("SEW", "124, 67");
            Instant savedForecastTimestamp = Instant.parse(savedForecast.getTimestamp());
            
            long timeElapsed = Duration.between(savedForecastTimestamp,currentTime).toMinutes();
            if (timeElapsed > ForecastPersistTime) {
                // get updated forecast by calling weather api
                JSONObject nwsForecastResponse = getForecast(gridInfo);

                // save forecast to dynamoDB
                Forecast updatedForecast = new Forecast();
                updatedForecast.setOffice(gridInfo.get(0));
                updatedForecast.setGridX_gridY(String.format("%s_%s",gridInfo.get(1), gridInfo.get(2)));
                updatedForecast.setTimestamp(currentTime.toString());
                updatedForecast.setForecast_content(nwsForecastResponse.toString());
                saveForecastToDynamoDB(updatedForecast);
                return "timeElapsed = " + timeElapsed + ". Forecast from nws API: " + nwsForecastResponse.toString();
            }
            return "timeElapsed = " + timeElapsed + ". Queried from dynamoDB: " + savedForecast.toString();
        } catch(Exception e) {
            ForecastDTO newForecastDTO = new ForecastDTO();
            newForecastDTO.setDefaultValues();
            newForecastDTO.setTimestamp(currentTime.toString());
            Forecast newForecast = new Forecast();
            BeanUtils.copyProperties(newForecastDTO, newForecast);
            saveForecastToDynamoDB(newForecast);
            return "Saving new forecast to DynamoDB" + e.toString();
        }
    }
}
