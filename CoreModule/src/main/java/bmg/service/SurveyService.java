package bmg.service;
import bmg.model.Survey;
import bmg.dto.SurveyData;
import bmg.dto.SurveyMetrics;
import bmg.model.Reservation;
import bmg.repository.SurveyRepository;
import bmg.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository SURVEY_REPO;
    private final UserRepository USER_REPO;
    private final ReservationService reservationService;

    /**
     * Saves a survey response
     * @param resId A reservation's id
     * @param guestId   A guest's id
     * @param surveyResponse A guest's survey response
     * @return a Survey object
     */
    public Survey saveSurvey(String resId, String guestId, String surveyResponse) {
        Reservation res = reservationService.findOne(resId);
        Survey s = new Survey();
        s.setGuestId(guestId);
        s.setSurveyResponse(surveyResponse);
        s.setReservationId(resId);
        s.setHostId(res.getHostId());
        s.setPropertyId(res.getPropertyId());
        s.setSubmissionTime(LocalDateTime.now());
        SURVEY_REPO.saveOne(s);
        return s;
    }

    /**
     * Finds list of surveys by the index
     * @param index
     * @param id
     * @return List of Survey Objects
     */
    public List<Survey> findSurveysByIndex(String index, String id) {
        List<Survey> surveys = new ArrayList<Survey>();
        if (index.equals("reservation")){
            surveys = SURVEY_REPO.findSurveysByReservation(id);
        } else if (index.equals("property")){
            surveys = SURVEY_REPO.findSurveysByIndex(Survey.Index.PROPERTY, id);
        } else if (index.equals("host")){
            surveys = SURVEY_REPO.findSurveysByIndex(Survey.Index.HOST, id);
        }
        return surveys;
    }

    /**
     * Finds survey metrics for host
     * @param id hostId
     * @return 
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    public SurveyMetrics getSurveyMetricsForHost(String id) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Survey> surveys = new ArrayList<Survey>();
        surveys = SURVEY_REPO.findSurveysByIndex(Survey.Index.HOST, id);

        SurveyMetrics surveyMetrics = new SurveyMetrics();
        surveyMetrics.setHostId(id);
        
        // // Key = overall, per property
        // Map<String, Object> result = new HashMap<>();
        // Map<String, Map<String, Integer>> combinedMetrics = new HashMap<>();
        List<SurveyData> surveyResponses = new ArrayList<>();
        for (Survey survey : surveys) {
            SurveyData surveyData = new SurveyData();
            surveyData.setReservationId(survey.getReservationId());
            surveyData.setProperty(reservationService.findOne(survey.getReservationId()).getProperty());
            surveyData.setGuest(USER_REPO.findUsersByUserId(survey.getGuestId()).get(0));
            surveyData.setSubmissionTime(survey.getSubmissionTime());
            surveyData.setSurveyResponse(survey.getSurveyResponse());
            surveyResponses.add(surveyData);

            Map<String, LinkedHashMap> surveyResponse = mapper.readValue(survey.getSurveyResponse(), Map.class);
            LinkedHashMap<String, Integer> qualityScores = surveyResponse.get("quality-rental");
            Map<String, Integer> qualityMetrics = new HashMap<>();
            // myList.add(map.get("quality"));
            for (String key : qualityScores.keySet()) {
                qualityMetrics.put(key, qualityScores.get(key));
                // Map<String, Integer> tally;
                // if (combinedMetrics.containsKey(key)) {
                //     tally = combinedMetrics.get(key);
                //     tally.put("value", tally.get("value") + qualityScores.get(key));
                //     tally.put("sampleSize", tally.get("sampleSize") + 1);

                // } else {
                //     tally = new HashMap<>();
                //     tally.put("value", qualityScores.get(key));
                //     tally.put("sampleSize", 1);
                // }
                // combinedMetrics.put(key, tally);
            }
            surveyData.setQualityMetrics(qualityMetrics);
        }
        surveyMetrics.setSurveyResponses(surveyResponses);
        //     result.put(survey.getReservationId(), Map.of("qualityMetrics", surveyMetrics, "submissionTime", survey.getSubmissionTime(), "guest", USER_REPO.findUsersByUserId(survey.getGuestId()), "surveyResponse", surveyResponse));
        // }
        // result.put("combinedMetrics", combinedMetrics);
        return surveyMetrics;
    }

    /**
     * Find the survey submitted by the guest for a particular reservation
     * @param resId A reservation Id
     * @param guestId A guest's id
     * @return A list of survey objects
     */
    public Survey findSurveyByReservationAndGuest(String resId, String guestId) {   
        Survey s = SURVEY_REPO.findSurveyByReservationAndGuest(resId, guestId);
        if (s == null) 
            throw new NoSuchElementException("Guest with id="+guestId+" has not submitted a survey for reservation=" + resId);
        return s;
    }
}
