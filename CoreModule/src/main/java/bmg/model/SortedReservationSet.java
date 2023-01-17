package bmg.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Groups a set of {@link Reservation}s according to status:
 *
 *      current:  checkIn <= today & checkOut >= now;
 *      upcoming: checkIn > today;
 *      past:     checkOut < now;
 */
@Builder
@Getter
public class SortedReservationSet {
    private final List<Reservation> CURRENT;
    private final List<Reservation> UPCOMING;
    private final List<Reservation> PAST;
}
