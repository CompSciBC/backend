package bmg.service;
import bmg.model.Survey;
import bmg.model.Reservation;
import bmg.repository.SurveyRepository;
import bmg.service.ReservationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository SURVEY_REPO;
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
     * Find the survey submitted by the guest for a particular reservation
     * @param resId A reservation Id
     * @param guestId A guest's id
     * @return A list of survey objects
     */
    public Survey findSurveyByReservationAndGuest(String resId, String guestId) {
        return SURVEY_REPO.findSurveyByReservationAndGuest(resId, guestId);
    }
}
