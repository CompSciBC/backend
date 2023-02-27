package bmg.service;

import bmg.dto.Address;
import bmg.model.Property;
import bmg.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Provides services for {@link Property} objects
 */
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository REPO;

    /**
     * Finds the property with the given id
     *
     * @param id A property id
     * @return The property with the given id
     */
    public Property findOne(String id) {
        Property property = REPO.findOne(id);

        if (property == null)
            throw new NoSuchElementException("Property with id="+id+" does not exist.");

        return property;
    }

    /**
     * Finds all properties with the given host id
     *
     * @param hostId A host id
     * @return A list of properties
     */
    public List<Property> findAll(String hostId) {
        return REPO.findAll(hostId);
    }

    /**
     * Saves the given list of properties
     *
     * @param properties A list of properties (max of 33 items)
     */
    public void saveAll(List<Property> properties) {
        REPO.saveAll(properties);
    }

    /**
     * Updates the identified property with the given updates
     *
     * @param id A property id
     * @param updates A map of attribute/value pairs
     */
    public void updateOne(String id, Map<String, Object> updates) {
        Property property = findOne(id);

        for (Map.Entry<String, Object> update : updates.entrySet()) {
            String attribute = update.getKey();
            Object value = update.getValue();

            switch (attribute.toLowerCase()) {
                case "hostid" -> property.setHostId((String)value);
                case "name" -> property.setName((String)value);
                case "address" -> property.setAddress((Address)value);
                default -> throw new IllegalArgumentException(
                        "Attribute \"" + attribute + "\" is not applicable or cannot be modified.");
            }
        }
        REPO.saveAll(List.of(property));
    }

    /**
     * Deletes the properties with the given ids
     *
     * @param ids A list of property ids (max of 25 items)
     */
    public void deleteAll(List<String> ids) {
        REPO.deleteAll(ids);
    }
}
