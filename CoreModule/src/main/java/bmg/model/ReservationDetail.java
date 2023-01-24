package bmg.model;

import lombok.Getter;

/**
 * Represents a reservation with its associated property details
 */
@Getter
public class ReservationDetail extends Reservation {
    private String propertyName;
    private String address;

    public ReservationDetail(Reservation reservation, Property property) {
        setId(reservation.getId());
        setHostId(property.getHostId());
        setPropertyId(property.getId());
        setGuestId(reservation.getGuestId());
        setNumGuests(reservation.getNumGuests());
        setCheckIn(reservation.getCheckIn());
        setCheckOut(reservation.getCheckOut());
        setReasonForStay(reservation.getReasonForStay());
        propertyName = property.getName();
        address = property.getAddress();
    }
}
