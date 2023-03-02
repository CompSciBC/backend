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
        // If a guest already submitted a survey for this reservation, update the survey response and submission time. If not, create a new survey object
        try {
            Survey submittedSurvey = findSurveyByReservationAndGuest(resId, guestId);
            s = submittedSurvey;
            s.setSurveyResponse(surveyResponse);
        } catch (NoSuchElementException e) {
            s.setGuestId(guestId);
            s.setSurveyResponse(surveyResponse);
            s.setReservationId(resId);
            s.setHostId(res.getHostId());
            s.setPropertyId(res.getPropertyId());
        }
        s.setSubmissionTime(LocalDateTime.now());
        SURVEY_REPO.saveOne(s);
        return s;
    }

    /**
     * Find all surveys associated with a reservation
     * @param resId reservation id
     * @return a List of Survey objects if at least one is found
     */
    public List<Survey> findAllSurveysByReservation(String resId) {
        List<Survey> surveyList = SURVEY_REPO.findSurveysByReservation(resId);
        if (surveyList.isEmpty()){
            throw new NoSuchElementException("No Survey has been submitted for Reservation with id="+resId+".");
        }
        return surveyList;
    }

    /**
     * Find the survey submitted by the guest for a particular reservation
     * @param resId A reservation Id
     * @param guestId A guest's id
     * @return A survey object if one is found
     */
    public Survey findSurveyByReservationAndGuest(String resId, String guestId) {
        List<Survey> surveyList = SURVEY_REPO.findSurveysByReservation(resId);
        for (Survey s : surveyList) {
            if (s.getGuestId().equals(guestId)){
                return s;
            }
        }
        throw new NoSuchElementException("No Survey has been submitted by Guest with id=" + guestId + " for Reservation with id="+resId+".");
    }

    /**
     * Find all surveys associated with a property
     * @param propId A property Id
     * @return a List of Survey objects if at least one is found
     */
    public List<Survey> findAllSurveysByProperty(String propId){
        List<Survey> surveyList = SURVEY_REPO.findSurveysByProperty(propId);
        if (surveyList.isEmpty()){
            throw new NoSuchElementException("No Survey has been submitted for Property with id="+propId+".");
        }
        return surveyList;
    }

    /**
     * Find all surveys associated with a host
     * @param hostId a Host id
     * @return a List of Survey objects if at least one is found
     */
    public List<Survey> findAllSurveysByHost(String hostId){
        List<Survey> surveyList = SURVEY_REPO.findSurveysByHost(hostId);
        if (surveyList.isEmpty()){
            throw new NoSuchElementException("No Survey has been submitted for Host with id="+hostId+".");
        }
        return surveyList;
    }
}
