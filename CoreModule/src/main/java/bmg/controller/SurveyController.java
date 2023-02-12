package bmg.controller;
import bmg.dto.SurveyResponse;
import bmg.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController extends Controller<Object> {
    private final SurveyService surveyService;

    /**
     * Saves survey result for a particular reservation
     *
     * @param invitation An invitation
     * @return A response entity containing the result of the email transaction
     */
    // TODO: change response type back to ResponseEntity<Response<Object>>
    @PostMapping("/{res_id}/{guest_id}/save-survey")
    public Object saveSurvey(@PathVariable(name = "res_id") String resId, @PathVariable(name = "guest_id") String guestId, @RequestBody SurveyResponse response) {
        // ResponseEntity test = new ResponseEntity<>(null);
        return surveyService.saveSurvey(resId, guestId, response);
        // return responseCodeOk(List.of("Survey saved successfully."));

    }

    @GetMapping("/{res_id}/find-survey")
    public Object findSurveyByReservation(@PathVariable(name = "res_id") String resId) {
        return surveyService.findSurveyByReservation(resId);
    }

    @GetMapping("/{res_id}/{guest_id}/find-survey")
    public Object findSurveyByReservation(@PathVariable(name = "res_id") String resId, @PathVariable(name = "guest_id") String guestId) {
        return surveyService.findSurveyByReservationAndGuest(resId, guestId);
    }
}
