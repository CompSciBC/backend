package bmg.controller;

import bmg.model.Reservation;
import bmg.dto.SortedReservationDetailSet;
import bmg.service.ReservationDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.util.List;

/**
 * Handles requests for {@link SortedReservationDetailSet} objects
 */
@RestController
@CrossOrigin
@RequestMapping("/api/reservations-by-status")
@RequiredArgsConstructor
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

        SortedReservationDetailSet set = SVC.findAllByStatus(index, id);
        return responseCodeOk(List.of(set));
    }
}
