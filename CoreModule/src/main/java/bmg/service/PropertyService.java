package bmg.service;

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
     * Saves the given property
     *
     * @param property A property
     */
    public void saveOne(Property property) {
        REPO.saveOne(property);
    }

    /**
     * Updates the identified property with the given updates
     *
     * @param id A property id
     * @param updates A map of attribute/value pairs
     */
    public void updateOne(String id, Map<String, String> updates) {
        Property property = findOne(id);

        for (Map.Entry<String, String> update : updates.entrySet()) {
            String attribute = update.getKey();
            String value = update.getValue();

            switch (attribute.toLowerCase()) {
                case "hostid" -> property.setHostId(value);
                case "name" -> property.setName(value);
                case "address" -> property.setAddress(value);
                default -> throw new IllegalArgumentException(
                        "Attribute \"" + attribute + "\" is not applicable or cannot be modified.");
            }
        }
        REPO.updateOne(id, property);
    }

    /**
     * Deletes the property with the given id
     *
     * @param id A property id
     */
    public void deleteOne(String id) {
        REPO.deleteOne(findOne(id));
    }
}
