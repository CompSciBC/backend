package bmg.controller;
import bmg.model.Forecast;
import bmg.service.NwsWeatherService;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@CrossOrigin
@RequestMapping("/api/weather")
@Log4j2
public class WeatherController {
    private final NwsWeatherService nwsWeatherService;

    public WeatherController(NwsWeatherService nwsWeatherService){
        this.nwsWeatherService = nwsWeatherService;
    }

    @GetMapping(value = {""})
    @Operation(summary = "Get the weather forecast for an address")
    public List<Forecast> getWeatherForecast(@RequestParam(name = "address") String address) throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException {
        log.info("Get weather at address={}", address);
        return nwsWeatherService.getTenDayForecast(address);
    }
}
