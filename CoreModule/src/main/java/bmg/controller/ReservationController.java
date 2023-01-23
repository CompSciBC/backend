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
     * Gets the reservation with the given id
     *
     * @param id A reservation id
     * @return A response entity containing a reservation
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response<Reservation>> getOne(@PathVariable(name = "id") String id) {
        Reservation reservation = SVC.findOne(id);
        return responseCodeOk(List.of(reservation));
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
     * Updates the identified reservation with the given updates
     *
     * @param id A reservation id
     * @param updates A map of attribute/value pairs
     * @return A response entity containing a reservation
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Response<Reservation>> updateOne(@PathVariable(name = "id") String id,
                                                           @RequestBody Map<String, Object> updates) {
        SVC.updateOne(id, updates);
        Reservation reservation = SVC.findOne(id);
        return responseCodeOk(List.of(reservation));
    }

    /**
     * Deletes the reservation with the given id
     *
     * @param id A reservation id
     * @return A response entity confirming the deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Reservation>> deleteOne(@PathVariable(name = "id") String id) {
        SVC.deleteOne(id);
        return responseCodeNoContent();
    }
}
