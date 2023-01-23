package bmg.service;

import bmg.model.Reservation;
import bmg.model.SortedReservationSet;
import bmg.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Provides services for {@link Reservation} objects
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository REPO;

    /**
     * Finds the reservation with the given id
     *
     * @param id A reservation id
     * @return The reservation with the given id
     */
    public Reservation findOne(String id) {
        Reservation reservation = REPO.findOne(id);

        if (reservation == null)
            throw new NoSuchElementException("Reservation with id="+id+" does not exist.");

        return reservation;
    }

    /**
     * Finds all reservations for the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @return A sorted reservation set
     */
    public SortedReservationSet findAllByStatus(Reservation.Index index, String id) {
        LocalDateTime now = LocalDateTime.now();
        return SortedReservationSet
                .builder()
                .CURRENT(findAllCurrent(index, id, now))
                .UPCOMING(findAllUpcoming(index, id, now))
                .PAST(findAlPast(index, id, now))
                .build();
    }

    /**
     * Finds all current reservations, as defined in {@link SortedReservationSet}
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param now The current date/time
     * @return A list of reservations
     */
    public List<Reservation> findAllCurrent(Reservation.Index index, String id, LocalDateTime now) {
        return REPO.findAllCheckInOnOrBeforeCheckOutAfter(
                index,
                id,
                LocalDateTime.of(now.toLocalDate(), LocalTime.MAX),
                now);
    }

    /**
     * Finds all upcoming reservations, as defined in {@link SortedReservationSet}
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param now The current date/time
     * @return A list of reservations
     */
    public List<Reservation> findAllUpcoming(Reservation.Index index, String id, LocalDateTime now) {
        return REPO.findAllCheckInAfter(
                index,
                id,
                LocalDateTime.of(now.toLocalDate(), LocalTime.MAX));
    }

    /**
     * Finds all past reservations, as defined in {@link SortedReservationSet}
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param now The current date/time
     * @return A list of reservations
     */
    public List<Reservation> findAlPast(Reservation.Index index, String id, LocalDateTime now) {
        return REPO.findAllCheckOutBefore(index, id, now);
    }

    /**
     * Saves the given reservation
     *
     * @param reservation A reservation
     */
    public void saveOne(Reservation reservation) {
        REPO.saveOne(reservation);
    }

    /**
     * Updates the identified reservation with the given updates
     *
     * @param id A reservation id
     * @param updates A map of attribute/value pairs
     */
    public void updateOne(String id, Map<String, Object> updates) {
        Reservation reservation = findOne(id);

        for (Map.Entry<String, Object> update : updates.entrySet()) {
            String attribute = update.getKey();
            Object value = update.getValue();

            switch (attribute.toLowerCase()) {
                case "hostid" -> reservation.setHostId((String) value);
                case "propertyid" -> reservation.setPropertyId((String) value);
                case "guestid" -> reservation.setGuestId((String) value);
                case "numguests" -> reservation.setNumGuests((Integer) value);
                case "checkin" -> reservation.setCheckIn(LocalDateTime.parse((String) value));
                case "checkout" -> reservation.setCheckOut(LocalDateTime.parse((String) value));
                case "reasonforstay" -> reservation.setReasonForStay((String) value);
                default -> throw new IllegalArgumentException(
                        "Attribute \"" + attribute + "\" is not applicable or cannot be modified.");
            }
        }
        REPO.saveOne(reservation);
    }

    /**
     * Deletes the reservation with the given id
     *
     * @param id A reservation id
     */
    public void deleteOne(String id) {
        REPO.deleteOne(findOne(id));
    }
}
