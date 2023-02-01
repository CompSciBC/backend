package bmg.service;

import bmg.model.Reservation;
import bmg.repository.QRCodeRepository;
import bmg.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Provides services for {@link Reservation} objects
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository REPO;
    private final QRCodeRepository QR_REPO;

    /**
     * Finds the primary reservation with the given id
     *
     * @param id A reservation id
     * @return The primary reservation with the given id
     */
    public Reservation findOne(String id) {
        List<Reservation> reservations = REPO.findAll(id, true);
        assertListNotEmpty(id, reservations);

        // there should never be more than one primary reservation with a given id
        assert(reservations.size() == 1);
        return reservations.get(0);
    }

    /**
     * Finds all reservations with the given id
     *
     * @param id A reservation id
     * @return A list of reservations
     */
    public List<Reservation> findAll(String id) {
        List<Reservation> reservations = REPO.findAll(id, false);
        assertListNotEmpty(id, reservations);
        return REPO.findAll(id, false);
    }

    /**
     * Finds all reservations by the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @return A list of reservations
     */
    public List<Reservation> findAll(Reservation.Index index, String id) {

        // if index is property or host, return the primary reservations only
        // if index is guest, return all reservations
        boolean primaryOnly = index != Reservation.Index.GUEST;
        return REPO.findAll(index, id, primaryOnly);
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
     * Updates an existing reservation with the given updates
     *
     * @param id A reservation id
     * @param updates A map of attribute/value pairs
     */
    public void updateAll(String id, Map<String, Object> updates) {

        // all existing reservations with the given id
        List<Reservation> reservations = findAll(id);

        // update each reservation
        for (Reservation reservation : reservations) {

            // for each updated attribute, set the new value on the current reservation item
            for (Map.Entry<String, Object> update : updates.entrySet()) {
                String attribute = update.getKey();
                Object value = update.getValue();

                switch (attribute.toLowerCase()) {
                    case "numguests" -> reservation.setNumGuests((Integer) value);
                    case "checkin" -> reservation.setCheckIn(LocalDateTime.parse((String) value));
                    case "checkout" -> reservation.setCheckOut(LocalDateTime.parse((String) value));
                    case "reasonforstay" -> reservation.setReasonForStay((String) value);
                    default -> throw new IllegalArgumentException(
                            "Attribute="+attribute+" is not applicable or cannot be modified.");
                }
            }
            REPO.updateOne(reservation);
        }
    }

    /**
     * Deletes all reservations with the given id as well as the associated invite QR code
     *
     * @param id A reservation id
     */
    public void deleteAll(String id) {
        REPO.deleteAll(id);
        QR_REPO.deleteOne(id);
    }

    /**
     * Throws a NoSuchElementException if the given list is empty
     *
     * @param id A reservation id
     * @param reservations A list of reservations
     */
    private void assertListNotEmpty(String id, List<Reservation> reservations) {
        if (reservations == null || reservations.size() == 0)
            throw new NoSuchElementException("Reservation with id="+id+" does not exist.");
    }
}
