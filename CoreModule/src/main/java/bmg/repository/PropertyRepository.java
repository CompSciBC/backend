package bmg.repository;

import bmg.model.Property;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTransactionWriteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.TransactionWriteRequest;
import com.amazonaws.services.dynamodbv2.model.TransactionCanceledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Provides CRUD operations for {@link Property} objects
 */
@Repository
@RequiredArgsConstructor
public class PropertyRepository {

    private final DynamoDBMapper MAPPER;

    /**
     * Finds the property with the given id
     *
     * @param id A property id
     * @return The property with the given id
     */
    public Property findOne(String id) {
        Property property = new Property();
        property.setId(id);
        return MAPPER.load(property);
    }

    /**
     * Finds all properties with given host id
     *
     * @param hostId A host id
     * @return A list of properties
     */
    public List<Property> findAll(String hostId) {
        Property property = new Property();
        property.setHostId(hostId);

        DynamoDBQueryExpression<Property> expression =
                new DynamoDBQueryExpression<Property>()
                        .withHashKeyValues(property)
                        .withIndexName(Property.HOST_ID_NAME_INDEX)
                        .withConsistentRead(false);

        return MAPPER.query(Property.class, expression);
    }

    /**
     * Saves the given property
     *
     * @param property A property
     */
    public void saveOne(Property property) {
        Property uniqueKey = uniqueKey(property);

        DynamoDBTransactionWriteExpression ifKeyDoesNotExist =
                new DynamoDBTransactionWriteExpression()
                        .withConditionExpression("attribute_not_exists(id)");

        try {
            MAPPER.transactionWrite(
                    new TransactionWriteRequest()
                            .addPut(uniqueKey, ifKeyDoesNotExist)
                            .addPut(property));

        } catch (TransactionCanceledException e) {
            handleDuplicateProperty(uniqueKey.getId());
        }
    }

    /**
     * Updates the identified property according to the given updated property
     *
     * @param id A property id
     * @param updatedProperty A property
     */
    public void updateOne(String id, Property updatedProperty) {
        Property oldKey = uniqueKey(findOne(id));
        Property newKey = uniqueKey(updatedProperty);

        if (oldKey.getId().equals(newKey.getId())) {
            MAPPER.save(updatedProperty);

        } else {
            try {
                MAPPER.transactionWrite(
                        new TransactionWriteRequest()
                                .addDelete(oldKey)
                                .addPut(newKey)
                                .addPut(updatedProperty));

            } catch (TransactionCanceledException e) {
                handleDuplicateProperty(newKey.getId());
            }
        }
    }

    /**
     * Deletes the given property
     *
     * @param property A property
     */
    public void deleteOne(Property property) {
        MAPPER.transactionWrite(
                new TransactionWriteRequest()
                        .addDelete(uniqueKey(property))
                        .addDelete(property));
    }

    /**
     * Generates a unique key for the purpose of enforcing unique constraints
     *
     * @param property A property
     * @return A property with id set to the string "hostId${:hostId}name${:name}";
     *         This is used to enforce a unique composite key for items in the database
     *         as suggested here:
     *         <a href="https://aws.amazon.com/blogs/database/simulating-amazon-dynamodb-unique-constraints-using-transactions/">see article</a>
     */
    private Property uniqueKey(Property property) {
        Property uniqueKey = new Property();
        uniqueKey.setId(String.format("hostId${%s}name${%s}", property.getHostId(), property.getName()));
        return uniqueKey;
    }

    /**
     * Throws an exception indicating a duplicate property
     *
     * @param uniqueKey The unique key of the duplicate property
     */
    private void handleDuplicateProperty(String uniqueKey) {
        throw new TransactionCanceledException("Duplicate property for "+uniqueKey+".");
    }
}
