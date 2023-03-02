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
        if (surveys.isEmpty()){
            throw new NoSuchElementException(String.format("No survey has been submitted for %s with id=%s", index.toString().toLowerCase(), id));
        }
        return surveys;
    }

    /**
     * Find the survey submitted by the guest for a particular reservation
     * @param resId A reservation Id
     * @param guestId A guest's id
     * @return A survey object if one is found
     */
    public List<Survey> findSurveyByReservationAndGuest(String resId, String guestId) {
        List<Survey> surveys = SURVEY_REPO.findSurveyByReservationAndGuest(resId, guestId);
        if (surveys.isEmpty()){
            throw new NoSuchElementException(String.format("No survey has been submitted for reservation with id=%s by guest with id=%s", resId, guestId));
        }
        return surveys;
    }
}
