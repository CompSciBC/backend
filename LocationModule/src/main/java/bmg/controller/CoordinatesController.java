package bmg.controller;

import java.util.concurrent.ExecutionException;

import bmg.dto.Coordinates;
import bmg.service.CoordinatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("api/coordinates")
public class CoordinatesController {
    @Autowired
    private CoordinatesService service;

    @GetMapping(value = "")
    public Coordinates getResult (            
        @RequestParam(required = false) String address) throws ExecutionException, InterruptedException {

        return service.getCoordinates(address);

    }
}

//@requestParam String "Address"

