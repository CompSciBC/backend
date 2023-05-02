package bmg.controller;

import java.util.concurrent.ExecutionException;

import bmg.dto.Coordinates;
import bmg.service.CoordinatesService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@CrossOrigin
@RestController
@RequestMapping("api/coordinates")
@Log4j2
public class CoordinatesController {
    @Autowired
    private CoordinatesService service;

    @GetMapping(value = "/{address}")
    public Coordinates getResult (            
        @PathVariable(name = "address") String address) throws ExecutionException, InterruptedException {
        log.info("Get coordinates at address={}", address);
        return service.getCoordinates(address);

    }
}



