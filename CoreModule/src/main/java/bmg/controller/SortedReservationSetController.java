package bmg.controller;

import bmg.model.Reservation;
import bmg.model.SortedReservationSet;
import bmg.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles requests for {@link SortedReservationSet} objects
 */
@RestController
@RequestMapping("/api/reservations-by-status")
@RequiredArgsConstructor
public class SortedReservationSetController extends Controller<SortedReservationSet> {
    private final ReservationService SVC;

    /**
     * Gets all reservations for the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @return A response entity containing a sorted reservation set
     */
    @GetMapping("")
    public ResponseEntity<Response<SortedReservationSet>> getAll(
            @RequestParam(name = "index") Reservation.Index index,
            @RequestParam(name = "id") String id) {

        SortedReservationSet set = SVC.findAllByStatus(index, id);
        return responseCodeOk(List.of(set));
    }
}
