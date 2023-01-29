package bmg.service;

import bmg.model.Property;
import bmg.model.Reservation;
import bmg.dto.ReservationDetail;
import bmg.dto.SortedReservationDetailSet;
import bmg.repository.PropertyRepository;
import bmg.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Provides services for {@link ReservationDetail} objects
 */
@Service
@RequiredArgsConstructor
public class ReservationDetailService {

    private final PropertyRepository P_REPO;
    private final ReservationRepository R_REPO;

    /**
     * Finds all reservations for the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @return A sorted reservation detail set
     */
    public SortedReservationDetailSet findAllByStatus(Reservation.Index index, String id) {
        LocalDateTime now = LocalDateTime.now();
        return SortedReservationDetailSet
                .builder()
                .CURRENT(findAllCurrent(index, id, now))
                .UPCOMING(findAllUpcoming(index, id, now))
                .PAST(findAllPast(index, id, now))
                .build();
    }

    /**
     * Converts a list of reservations into a list of reservation details
     *
     * @param reservations A list of reservations
     * @return A list of reservation details
     */
    private List<ReservationDetail> convertToDetail(List<Reservation> reservations) {
        List<ReservationDetail> details = new ArrayList<>();
        HashMap<String, Property> properties = new HashMap<>();

        for (Reservation reservation : reservations) {
            String propertyId = reservation.getPropertyId();

            if (!properties.containsKey(propertyId))
                // store properties already seen to prevent duplicate queries
                properties.put(propertyId, P_REPO.findOne(propertyId));

            details.add(new ReservationDetail(reservation, properties.get(propertyId)));
        }
        return details;
    }

    /**
     * Finds all current reservations, as defined in {@link SortedReservationDetailSet}
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param now The current date/time
     * @return A list of reservation details
     */
    private List<ReservationDetail> findAllCurrent(Reservation.Index index, String id, LocalDateTime now) {
        return convertToDetail(
                R_REPO.findAllCheckInOnOrBeforeCheckOutAfter(
                        index,
                        id,
                        index != Reservation.Index.GUEST,
                        LocalDateTime.of(now.toLocalDate(), LocalTime.MAX),
                        now));
    }

    /**
     * Finds all upcoming reservations, as defined in {@link SortedReservationDetailSet}
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param now The current date/time
     * @return A list of reservation details
     */
    private List<ReservationDetail> findAllUpcoming(Reservation.Index index, String id, LocalDateTime now) {
        return convertToDetail(
                R_REPO.findAllCheckInAfter(
                        index,
                        id,
                        index != Reservation.Index.GUEST,
                        LocalDateTime.of(now.toLocalDate(), LocalTime.MAX)));
    }

    /**
     * Finds all past reservations, as defined in {@link SortedReservationDetailSet}
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param now The current date/time
     * @return A list of reservation details
     */
    private List<ReservationDetail> findAllPast(Reservation.Index index, String id, LocalDateTime now) {
        return convertToDetail(
                R_REPO.findAllCheckOutBefore(
                        index,
                        id,
                        index != Reservation.Index.GUEST,
                        now));
    }
}
