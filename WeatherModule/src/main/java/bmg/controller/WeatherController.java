package bmg.controller;
import bmg.service.NwsWeatherService;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
@RequestMapping("/api/weather")
public class WeatherController {
    private final NwsWeatherService nwsWeatherService;

    public WeatherController(NwsWeatherService nwsWeatherService){
        this.nwsWeatherService = nwsWeatherService;
    }

    @GetMapping(value = {""})
    public Object getWeatherForecast() throws URISyntaxException, IOException {
        return nwsWeatherService.returnWeatherReport();
    }

    // TODO: delete after demo to group
    @GetMapping(value = {"/dynamoDemo"})
    public Object getDummyWeatherData() throws URISyntaxException, IOException {
        return nwsWeatherService.saveAndRetrieveObjectFromDynamoDBDemo();
    }

}
