package bmg.controller;

import bmg.dto.AmenitySuggestion;
import bmg.service.AmenitySuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles requests for {@link AmenitySuggestion}s
 */
@RestController
@CrossOrigin
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
@Log4j2
public class AmenitySuggestionController extends Controller<AmenitySuggestion> {

    private final AmenitySuggestionService SVC;

    /**
     * Gets amenity suggestions for the given image
     *
     * @param imageUrl An image url
     * @return A response entity containing a list of amenity suggestions
     */
    @GetMapping("")
    public ResponseEntity<Response<AmenitySuggestion>> getSuggestions(@RequestParam(name = "url") String imageUrl) {
        log.info("Get amenity suggestions for imageUrl={}", imageUrl);

        List<AmenitySuggestion> data = SVC.getSuggestions(imageUrl).stream().toList();
        return responseCodeOk(data);
    }
}
