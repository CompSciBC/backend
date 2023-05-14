package bmg.controller;

import bmg.model.Reservation;
import bmg.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Handles requests for {@link Reservation} objects
 */
@RestController
@CrossOrigin
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Log4j2
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

        log.info("Get all reservations with {}={}", index.getKEY(), id);

        List<Reservation> reservations = SVC.findAll(index, id);
        return responseCodeOk(reservations);
    }

    /**
     * Gets all reservations for the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @return A response entity containing a list of reservations
     */
    @GetMapping("/checkoutafter")
    public ResponseEntity<Response<Reservation>> findAllCheckOutAfter(
            @RequestParam(name = "index") Reservation.Index index,
            @RequestParam(name = "id") String id,
            @RequestParam(name = "primaryOnly") boolean primaryOnly,
            @RequestParam(name = "checkOutCutOff") LocalDateTime cutoff
            ) {

        log.info("Get all primary reservations with {}={} and checkOut date after", index.getKEY(), id, cutoff);

        List<Reservation> reservations = SVC.findAllCheckOutAfter(index, id, primaryOnly, cutoff);
        return responseCodeOk(reservations);
    }

    /**
     * Gets all reservations for the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @return A response entity containing a list of reservations
     */
    @GetMapping("/checkinafter")
    public ResponseEntity<Response<Reservation>> findAllCheckInAfter(
            @RequestParam(name = "index") Reservation.Index index,
            @RequestParam(name = "id") String id,
            @RequestParam(name = "primaryOnly") boolean primaryOnly,
            @RequestParam(name = "checkInCutOff") LocalDateTime cutoff
            ) {

        log.info("Get all primary reservations with {}={} and checkOut date after", index.getKEY(), id, cutoff);

        List<Reservation> reservations = SVC.findAllCheckInAfter(index, id, primaryOnly, cutoff);
        return responseCodeOk(reservations);
    }

    /**
     * Gets all reservations with the given id
     *
     * @param id A reservation id
     * @param primary Set to true to filter reservations for only the primary entry,
     *                or false/undefined to filter for only non-primary entries
     * @return A response entity containing a list of reservations
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response<Reservation>> getAll(
            @PathVariable(name = "id") String id,
            @RequestParam(name = "primary", required = false) Boolean primary) {

        log.info("Get all {}primary reservations with id={}", primary != null && primary ? "" : "non-", id);

        List<Reservation> reservations;

        if (primary == null || !primary)
            // find all non-primary reservations with this id
            reservations = SVC.findAll(id, true);
        else
            // find only the primary reservation with this id
            reservations = List.of(SVC.findOne(id));

        return responseCodeOk(reservations);
    }

    /**
     * Saves the given list of reservations
     *
     * @param reservations A list of reservations
     * @return A response entity containing a list of reservations
     */
    @PostMapping("")
    public ResponseEntity<Response<Reservation>> saveAll(@RequestBody List<Reservation> reservations) {
        log.info("Save reservations:");
        reservations.forEach((r) -> log.info("\t{}", r));

        SVC.saveAll(reservations);

        String location = reservations.size() == 1
                ? reservations.get(0).getId()
                : "...";

        return responseCodeCreated(reservations, "/"+location);
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

        log.info("Update all reservations with id={}:", id);
        updates.forEach((key, value) -> log.info("\t{}={}", key, value));

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
        log.info("Delete all reservations with id={}", id);

        SVC.deleteAll(id);
        return responseCodeNoContent();
    }
}
