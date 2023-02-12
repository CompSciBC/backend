package bmg.service;
import bmg.dto.SurveyResponse;
import bmg.model.Survey;
import bmg.model.Reservation;
import bmg.repository.SurveyRepository;
import bmg.service.ReservationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

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
    public String saveSurvey(String resId, String guestId, SurveyResponse surveyResponse) {
        try {
            // Example Reservation Object: Reservation(id=test-res-1, guestId=test-guest-1, hostId=test-host-1, propertyId=test-prop-1, numGuests=2, checkIn=2023-01-01T12:00, checkOut=2099-01-01T12:00, reasonForStay=business, isPrimary=true)
            Reservation res = reservationService.findOne(resId);
            Survey s = new Survey();
            s.setGuestId(guestId);
            s.setSurveyResponse(surveyResponse.getSurveyResponse());
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
    
}
