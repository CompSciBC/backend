package bmg.service;
import bmg.dto.SurveyResponse;
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
     * Saves survey response
     *
     * @param resId A reservation id
     * @param surveyResponse A guest's survey response
     */
    // TODO: change return type back to void
    // TODO: change setId to something actual
    public String saveSurvey(String resId, String guestId, String surveyResponse) {
        try {
            // return surveyResponse;
            // Example Reservation Object: Reservation(id=test-res-1, guestId=test-guest-1, hostId=test-host-1, propertyId=test-prop-1, numGuests=2, checkIn=2023-01-01T12:00, checkOut=2099-01-01T12:00, reasonForStay=business, isPrimary=true)
            Reservation res = reservationService.findOne(resId);
            Survey s = new Survey();
            s.setGuestId(guestId);
            s.setSurveyResponse(surveyResponse);
            s.setReservationId(resId);
            s.setSubmissionTime(LocalDateTime.now());
            s.setHostId(res.getHostId());
            s.setPropertyId(res.getPropertyId());
            s.setId(resId);
            SURVEY_REPO.saveOne(s);
            return s.toString();
        } catch(Exception e) {
            return e.getMessage();
        }
    }

    public String findSurveyByReservation(String resId) {
        try {
            // Example Reservation Object queried back: Survey(id=hndo-test-res-1, guestId=hndo_guest1, hostId=hndo_host1, reservationId=hndo-test-res-1, propertyId=hndo-prop-1, submissionTime=2023-02-11T21:43:25, surveyResponse=some_response)
            List<Survey> surveyList = SURVEY_REPO.findSurveyByReservation(resId);
            return surveyList.get(0).toString();
        } catch(Exception e) {
            return e.getMessage();
        }
    }

    public String findSurveyByReservationAndGuest(String resId, String guestId) {
        try {
            // Example Reservation Object queried back: Survey(id=hndo-test-res-1, guestId=hndo_guest1, hostId=hndo_host1, reservationId=hndo-test-res-1, propertyId=hndo-prop-1, submissionTime=2023-02-11T21:43:25, surveyResponse=some_response)
            List<Survey> surveyList = SURVEY_REPO.findSurveyByReservation(resId);
            if (surveyList.size() == 0) {
                throw new Exception("no survey found for given reservation");
            }
            for (Survey s : surveyList) {
                if (s.getGuestId().equals(guestId)){
                    return s.toString();
                }
            }
            throw new Exception("no survey found for given reservation and guest");
        } catch(Exception e) {
            return e.getMessage();
        }
    }
    
}
