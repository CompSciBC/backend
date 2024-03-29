package bmg.controller;

import bmg.model.Property;
import bmg.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Handles requests for {@link Property} objects
 */
@RestController
@CrossOrigin
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Log4j2
public class PropertyController extends Controller<Property> {

    private final PropertyService SVC;

    /**
     * Gets the property with the given id
     *
     * @param id A property id
     * @return A response entity containing a property
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response<Property>> getOne(@PathVariable(name = "id") String id) {
        log.info("Get property with id={}", id);

        Property property = SVC.findOne(id);
        return responseCodeOk(List.of(property));
    }

    /**
     * Gets all properties with the given host id
     *
     * @param hostId A host id
     * @return A response entity containing a list of properties
     */
    @GetMapping("")
    public ResponseEntity<Response<Property>> getAll(@RequestParam(name = "hostId") String hostId) {
        log.info("Get all properties with hostId={}", hostId);

        List<Property> property = SVC.findAll(hostId);
        return responseCodeOk(property);
    }

    /**
     * Saves the given list of properties
     *
     * @param properties A list of properties (max of 33 items)
     * @return A response entity containing a list of properties
     */
    @PostMapping("")
    public ResponseEntity<Response<Property>> saveAll(@RequestBody List<Property> properties) {
        log.info("Save properties:");
        properties.forEach((p) -> log.info("\t{}", p));

        SVC.saveAll(properties);

        String location = properties.size() == 1
                ? properties.get(0).getId()
                : "...";

        return responseCodeCreated(properties, "/"+location);
    }

    /**
     * Updates the identified property with the given updates
     *
     * @param id A property id
     * @param updates A map of attribute/value pairs
     * @return A response entity containing a property
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Response<Property>> updateOne(@PathVariable(name = "id") String id,
                                                        @RequestBody Map<String, Object> updates) {
        log.info("Update property with id={}:", id);
        updates.forEach((key, value) -> log.info("\t{}={}", key, value));

        SVC.updateOne(id, updates);
        Property property = SVC.findOne(id);
        return responseCodeOk(List.of(property));
    }

    /**
     * Deletes the property with the given id
     *
     * @param id A property id
     * @return A response entity confirming the deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Property>> deleteOne(@PathVariable(name = "id") String id) {
        log.info("Delete property with id={}", id);

        SVC.deleteAll(List.of(id));
        return responseCodeNoContent();
    }

    /**
     * Deletes the properties with the given ids
     *
     * @param ids A list of property ids (max of 25 items)
     * @return A response entity confirming the deletion
     */
    @DeleteMapping("")
    public ResponseEntity<Response<Property>> deleteAll(@RequestBody List<String> ids) {
        log.info("Delete properties:");
        ids.forEach((i) -> log.info("\t{}", i));

        SVC.deleteAll(ids);
        return responseCodeNoContent();
    }
}
