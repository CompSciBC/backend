package bmg.controller;

import java.util.concurrent.ExecutionException;

import bmg.dto.Coordinates;
import bmg.service.CoordinatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("api/coordinates")
public class CoordinatesController {
    @Autowired
    private CoordinatesService service;

    @GetMapping(value = "/{address}")
    public Coordinates getResult (            
        @PathVariable(name = "address") String address) throws ExecutionException, InterruptedException {

        return service.getCoordinates(address);

    }
}

//@requestParam String "Address"

