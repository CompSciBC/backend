package bmg.controller;

import bmg.dto.Place;
import bmg.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService svc;

    /**
     * Calls the PlaceService to handle to logic to return a List of Places
     * @param address provided by path
     * @return a List of Places
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @GetMapping("/{address}")
    public List<Place> getAll(@PathVariable String address) throws IOException, InterruptedException, ExecutionException {
        return svc.getPlaces(address);
    }
}
