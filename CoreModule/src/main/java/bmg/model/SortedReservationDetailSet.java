package bmg.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Groups a set of {@link ReservationDetail}s according to status:
 *
 *      current:  checkIn <= today & checkOut >= now;
 *      upcoming: checkIn > today;
 *      past:     checkOut < now;
 */
@Builder
@Getter
public class SortedReservationDetailSet {
    private final List<ReservationDetail> CURRENT;
    private final List<ReservationDetail> UPCOMING;
    private final List<ReservationDetail> PAST;
}
