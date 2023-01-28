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
        setGuestId(reservation.getGuestId());
        setHostId(property.getHostId());
        setPropertyId(property.getId());
        setNumGuests(reservation.getNumGuests());
        setCheckIn(reservation.getCheckIn());
        setCheckOut(reservation.getCheckOut());
        setReasonForStay(reservation.getReasonForStay());
        setIsPrimary(reservation.getIsPrimary());
        propertyName = property.getName();
        address = property.getAddress();
    }
}
