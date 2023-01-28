package bmg.controller;

import bmg.model.Reservation;
import bmg.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Handles requests for {@link Reservation} objects
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController extends Controller<Reservation> {

    private final ReservationService SVC;

    /**
     * Gets all reservations for the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @return A response entity containing a list of reservations
     */
    @GetMapping("")
    public ResponseEntity<Response<Reservation>> getAll(
            @RequestParam(name = "index") Reservation.Index index,
            @RequestParam(name = "id") String id) {

        List<Reservation> reservations = SVC.findAll(index, id);
        return responseCodeOk(reservations);
    }

    /**
     * Gets all reservations with the given id
     *
     * @param id A reservation id
     * @return A response entity containing a list of reservations
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response<Reservation>> getAll(
            @PathVariable(name = "id") String id,
            @RequestParam(name = "primary", required = false) Boolean primary) {

        List<Reservation> reservations;

        if (primary == null || !primary)
            // find all reservations with this id
            reservations = SVC.findAll(id);
        else
            // find only the primary reservation with this id
            reservations = List.of(SVC.findOne(id));

        return responseCodeOk(reservations);
    }

    /**
     * Creates a new reservation
     *
     * @param reservation A reservation
     * @return A response entity containing a reservation
     */
    @PostMapping("")
    public ResponseEntity<Response<Reservation>> createOne(@RequestBody Reservation reservation) {
        SVC.saveOne(reservation);
        return responseCodeCreated(List.of(reservation), "/"+reservation.getId());
    }

    /**
     * Updates the identified reservation(s) with the given updates
     *
     * @param id A reservation id
     * @param updates A map of attribute/value pairs
     * @return A response entity containing a reservation
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Response<Reservation>> updateAll(@PathVariable(name = "id") String id,
                                                           @RequestBody Map<String, Object> updates) {
        SVC.updateAll(id, updates);
        Reservation reservation = SVC.findOne(id);
        return responseCodeOk(List.of(reservation));
    }

    /**
     * Deletes all reservations with the given id
     *
     * @param id A reservation id
     * @return A response entity confirming the deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Reservation>> deleteAll(@PathVariable(name = "id") String id) {
        SVC.deleteAll(id);
        return responseCodeNoContent();
    }
}
