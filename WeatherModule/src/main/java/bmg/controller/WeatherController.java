package bmg.controller;
import bmg.service.NwsWeatherService;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

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
    public Object getWeatherForecast(@RequestParam(name = "address") String address) throws InterruptedException, ExecutionException {
        return nwsWeatherService.getTenDayForecast(address).toString();
    }
}
