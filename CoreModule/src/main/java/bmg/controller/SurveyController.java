package bmg.controller;
import bmg.model.Survey;
import bmg.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController extends Controller<Survey> {
    private final SurveyService surveyService;

    /**
     * Saves survey submitted by a guest for a particular reservation
     * @param resId A reservation id
     * @param guestId  A guest id
     * @param guestSurveyResponse   A guest's survey response
     * @return A response entity containing the survey just submitted
     */
    @PostMapping("/save")
    public ResponseEntity<Response<Survey>> saveSurvey(@RequestParam(required = true) String resId, @RequestParam(required = true) String guestId, @RequestBody String guestSurveyResponse) {
        Survey s = surveyService.saveSurvey(resId, guestId, guestSurveyResponse);
        return responseCodeCreated(List.of(s), "/" + s.getReservationId());
    }

    /**
     * Finds all surveys submitted for a particular host, reservation, or property
     * @param resId A reservation id
     * @param id The id of a host, reservation, or property
     * @return A response entity containing a list of surveys
     */
    @GetMapping("")
    @Operation(summary = "Get list of surveys by index")
    public ResponseEntity<Response<Survey>> findSurveysByIndex(@RequestParam(required = true) String index, @RequestParam(required = true) String id) {
        List<Survey> surveys = surveyService.findSurveysByIndex(index, id);
        return responseCodeOk(surveys); 
    }

    /**
     * Finds the survey submitted by a guest for a reservation
     * @param resId
     * @param guestId
     * @return
     */
    @GetMapping("/{res_id}/{guest_id}")
    public ResponseEntity<Response<Survey>> findSurveyByReservationAndGuest(@PathVariable(name = "res_id") String resId, @PathVariable(name = "guest_id") String guestId) {
        try {
            Survey s = surveyService.findSurveyByReservationAndGuest(resId, guestId);
            return responseCodeOk(List.of(s));
        } catch (Exception e) {
            return responseCodeNoContent();
        }
    }
}
