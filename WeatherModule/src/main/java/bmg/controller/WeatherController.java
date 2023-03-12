package bmg.controller;
import bmg.model.Forecast;
import bmg.service.NwsWeatherService;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


@RestController
@CrossOrigin
@RequestMapping("/api/weather")
public class WeatherController {
    private final NwsWeatherService nwsWeatherService;

    public WeatherController(NwsWeatherService nwsWeatherService){
        this.nwsWeatherService = nwsWeatherService;
    }

    @GetMapping(value = {""})
    public List<Forecast> getWeatherForecast(@RequestParam(name = "address") String address) throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException {
        return nwsWeatherService.getTenDayForecast(address);
    }
}
