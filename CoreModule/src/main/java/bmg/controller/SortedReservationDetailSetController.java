package bmg.controller;

import bmg.model.Reservation;
import bmg.dto.SortedReservationDetailSet;
import bmg.service.ReservationDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles requests for {@link SortedReservationDetailSet} objects
 */
@RestController
@CrossOrigin
@RequestMapping("/api/reservations-by-status")
@RequiredArgsConstructor
@Log4j2
public class SortedReservationDetailSetController extends Controller<SortedReservationDetailSet> {

    private final ReservationDetailService SVC;

    /**
     * Gets all reservations for the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @return A response entity containing a sorted reservation detail set
     */
    @GetMapping("")
    public ResponseEntity<Response<SortedReservationDetailSet>> getAll(
            @RequestParam(name = "index") Reservation.Index index,
            @RequestParam(name = "id") String id) {

        log.info("Get all reservations with {}={}", index.getKEY(), id);

        SortedReservationDetailSet set = SVC.findAllByStatus(index, id);
        return responseCodeOk(List.of(set));
    }
}
