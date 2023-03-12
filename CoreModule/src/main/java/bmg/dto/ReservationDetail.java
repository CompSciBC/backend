package bmg.dto;

import bmg.model.Property;
import bmg.model.Reservation;
import lombok.Getter;

/**
 * Represents a reservation with its associated property details
 */
@Getter
public class ReservationDetail extends Reservation {
    private final Property PROPERTY;

    public ReservationDetail(Reservation reservation, Property property) {
        setId(reservation.getId());
        setGuestId(reservation.getGuestId());
        setHostId(reservation.getHostId());
        setPropertyId(property.getId());
        setNumGuests(reservation.getNumGuests());
        setCheckIn(reservation.getCheckIn());
        setCheckOut(reservation.getCheckOut());
        setReasonForStay(reservation.getReasonForStay());
        setInviteCode(reservation.getInviteCode());
        this.PROPERTY = property;
    }
}
