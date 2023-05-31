package bmg.controller;

import bmg.dto.Place;
import bmg.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService svc;

    /**
     * Calls the PlaceService to handle the logic to return a List of Places
     * @param address provided by path
     * @return a List of Places
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @GetMapping("/{address}")
    public List<Place> getAll(@PathVariable String address) throws IOException, InterruptedException, ExecutionException, SQLException {
        return svc.getPlaces(address);
    }
}