package bmg.service;
import bmg.model.Survey;
import bmg.model.Property;
import bmg.dto.PieChartDataPoint;
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
    private final PropertyService propertyService;

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
        List<Survey> surveys = SURVEY_REPO.findSurveysByIndex(Survey.Index.HOST, id);

        SurveyMetrics surveyMetrics = new SurveyMetrics();
        surveyMetrics.setHostId(id);
        surveyMetrics.setSurveyResponses(getSurveyResponses(surveys));

        // Construct pie chart data for each property reviewed
        List<Property> propertiesManaged = propertyService.findAll(id);
        Map<String, List<PieChartDataPoint>> pieChartData = new HashMap<>();
        for (Property p: propertiesManaged) {
            List<Survey> surveysByProperty = findSurveysByIndex("property", p.getId());
            pieChartData.put(p.getName(), getSurveyPieChartData(surveysByProperty));
        };
        surveyMetrics.setPieChartData(pieChartData);
        return surveyMetrics;
    }

    private List<PieChartDataPoint> getSurveyPieChartData(List<Survey> surveys) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, PieChartDataPoint> data = new HashMap<>();
        // For each survey
        for (Survey survey : surveys) {
            // Extract guest's quality ratings from the survey response
            Map<String, LinkedHashMap> surveyResponse = mapper.readValue(survey.getSurveyResponse(), Map.class);
            LinkedHashMap<String, Integer> qualityScores = surveyResponse.get("quality-rental");
            // For each rating in the set of quality ratings, add to the tally of 1s, 2s, ... 5s ratings
            for (String key : qualityScores.keySet()) {
                if (data.containsKey(key)) {
                    PieChartDataPoint datum = data.get(key);
                    datum.setCount(datum.getCount() + 1);
                    Map<Integer, Integer> m = datum.getRatingFrequencyMap();
                    if (m.containsKey(qualityScores.get(key))) {
                        int tally = m.get(qualityScores.get(key));
                        m.put(qualityScores.get(key), tally + 1);
                    } else {
                        m.put(qualityScores.get(key), 1);
                    }

                } else {
                    PieChartDataPoint datum = new PieChartDataPoint();
                    datum.setName(key);
                    datum.setCount(1);
                    Map<Integer, Integer> m = new HashMap<>();
                    m.put(qualityScores.get(key), 1);
                    datum.setRatingFrequencyMap(m);
                    data.put(key, datum);
                }
            }
        }
        List<PieChartDataPoint> pieChartData = new ArrayList<>();
        for (String key : data.keySet()) {
            pieChartData.add(data.get(key));
        }
        return pieChartData;
    }

    private List<SurveyData> getSurveyResponses(List<Survey> surveys) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
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
            int qualityMetricsAverage = 0;
            Map<String, Integer> qualityMetrics = new HashMap<>();

            for (String key : qualityScores.keySet()) {
                qualityMetrics.put(key, qualityScores.get(key));
                qualityMetricsAverage += qualityScores.get(key);
            }
            surveyData.setQualityMetrics(qualityMetrics);
            surveyData.setQualityMetricsAverage(qualityMetricsAverage * 1.0 /(qualityScores.keySet().size()));
        }
        return surveyResponses;
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
