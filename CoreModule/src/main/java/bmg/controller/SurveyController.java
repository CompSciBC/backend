package bmg.controller;
import bmg.dto.SurveyMetrics;
import bmg.model.Survey;
import bmg.service.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
@Log4j2
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
        log.info("Save survey:");
        log.info("\tresId={}", resId);
        log.info("\tguestId={}", guestId);
        log.info("\tResponses:\n{}", guestSurveyResponse);

        Survey s = surveyService.saveSurvey(resId, guestId, guestSurveyResponse);
        return responseCodeCreated(List.of(s), "/" + s.getReservationId());
    }

    /**
     * Finds all surveys submitted for a particular host, reservation, or property
     * @param index A survey index
     * @param id The id of a host, reservation, or property
     * @return A response entity containing a list of surveys
     */
    @GetMapping("")
    @Operation(summary = "Get list of surveys by index")
    public ResponseEntity<Response<Survey>> findSurveysByIndex(@RequestParam(required = true) String index, @RequestParam(required = true) String id) {
        log.info("Find surveys with {}={}", index, id);

        List<Survey> surveys = surveyService.findSurveysByIndex(index, id);
        return responseCodeOk(surveys); 
    }

    /**
     * Finds survey metrics for host
     * @param id The id of a host
     * @return 
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @GetMapping("/hostmetrics")
    @Operation(summary = "Finds survey metrics for host")
    public ResponseEntity<SurveyMetrics> getSurveyMetricsForHost(@RequestParam(required = true) String id) throws JsonMappingException, JsonProcessingException {
        log.info("Find survey metrics for host={}", id);
        SurveyMetrics surveyMetrics = surveyService.getSurveyMetricsForHost(id);
        return ResponseEntity.ok(surveyMetrics);
    }

    /**
     * Finds the survey submitted by a guest for a reservation
     * @param resId
     * @param guestId
     * @return
     */
    @GetMapping("/{res_id}/{guest_id}")
    public ResponseEntity<Response<Survey>> findSurveyByReservationAndGuest(@PathVariable(name = "res_id") String resId, @PathVariable(name = "guest_id") String guestId) {
        log.info("Find surveys with resId={}, guestId={}", resId, guestId);

        try {
            Survey s = surveyService.findSurveyByReservationAndGuest(resId, guestId);
            return responseCodeOk(List.of(s));
        } catch (Exception e) {
            log.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
            return responseCodeNoContent();
        }
    }
}
