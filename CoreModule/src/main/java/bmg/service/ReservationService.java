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
    private final RandomStringGenerator RSG;

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
     * @param nonPrimaryOnly If true, returns only the non-primary reservation entries
     * @return A list of reservations
     */
    public List<Reservation> findAll(String id, boolean nonPrimaryOnly) {
        List<Reservation> reservations = REPO.findAll(id, false);
        assertListNotEmpty(id, reservations);
        return nonPrimaryOnly
                ? reservations.stream().filter((r) -> !r.getIsPrimary()).toList()
                : reservations;
    }

    /**
     * Finds all reservations with the given id and a check-out date/time after the cutoff
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param primaryOnly If true, returns only primary reservation entries
     * @param cutoff A date/time (exclusive)
     * @return A list of reservations
     */
    public List<Reservation> findAllCheckOutAfter(Reservation.Index index,
                                                   String id,
                                                   boolean primaryOnly,
                                                   LocalDateTime cutoff) {

        List<Reservation> reservations = REPO.findAllCheckOutAfter(index, id, primaryOnly, cutoff);
        assertListNotEmpty(id, reservations);

        return reservations;
    }

    /**
     * Finds all reservations with the given id and a check-in date/time after the cutoff
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param primaryOnly If true, returns only primary reservation entries
     * @param cutoff A date/time (exclusive)
     * @return A list of reservations
     */
    public List<Reservation> findAllCheckInAfter(Reservation.Index index,
                                                   String id,
                                                   boolean primaryOnly,
                                                   LocalDateTime cutoff) {

        List<Reservation> reservations = REPO.findAllCheckInAfter(index, id, primaryOnly, cutoff);
        assertListNotEmpty(id, reservations);
        return reservations;
    }

    /**
     * Finds all reservations with the given id and a check-in date/time after the cutoff
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param primaryOnly If true, returns only primary reservation entries
     * @param cutoff A date/time (exclusive)
     * @return A list of reservations
     */
    public List<Reservation> findAllCheckInOnOrBeforeCheckOutAfter(Reservation.Index index,
                                                   String id,
                                                   boolean primaryOnly,
                                                   LocalDateTime checkInCutOff, LocalDateTime checkOutCutOff) {

        List<Reservation> reservations = REPO.findAllCheckInOnOrBeforeCheckOutAfter(index, id, primaryOnly, checkInCutOff, checkOutCutOff);
        assertListNotEmpty(id, reservations);
        return reservations;
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
     * Finds all reservations by the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @return A list of reservations
     */
    public List<Reservation> findAllPrimaryOnlyFalse(Reservation.Index index, String id) {

        // if index is property or host, return the primary reservations only
        // if index is guest, return all reservations
        // boolean primaryOnly = index != Reservation.Index.GUEST;
        return REPO.findAll(index, id, false);
    }

    /**
     * Saves the given list of reservations
     *
     * @param reservations A list of reservations
     */
    public void saveAll(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            try {
                // findOne will throw an error if either:
                //      a) id is null
                //      b) id does not exist in the database
                // in either case, this means that the reservation is primary
                Reservation existingPrimary = findOne(reservation.getId());

                // id already exists in the database; if this reservation has the same
                // guest id as the primary, then this is the primary, otherwise, it is not
                boolean same = existingPrimary.getGuestId().equals(reservation.getGuestId());
                reservation.setIsPrimary(same);

                // this field should not be modified by client, so setting it here to prevent overwrite
                reservation.setInviteCode(existingPrimary.getInviteCode());

            } catch (Exception e) {
                // this is the primary (first of its kind)
                reservation.setIsPrimary(true);

                // generate random 12 character invite code
                reservation.setInviteCode(RSG.generate(12));
            }
            REPO.saveAll(List.of(reservation));
        }
    }

    /**
     * Updates all reservations with the given id with the given updates
     *
     * @param id A reservation id
     * @param updates A map of attribute/value pairs
     */
    public void updateAll(String id, Map<String, Object> updates) {

        // all existing reservations with the given id
        List<Reservation> reservations = findAll(id, false);

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
                    case "checkedin" -> reservation.setCheckedIn((Boolean) value);
                    default -> throw new IllegalArgumentException(
                            "Attribute="+attribute+" is not applicable or cannot be modified.");
                }
            }
        }
        REPO.saveAll(reservations);
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
