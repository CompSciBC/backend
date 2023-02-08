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
     * Saves the given list of properties. If any property fails to save,
     * the transaction is cancelled and no properties are saved.
     *
     * @param properties A list of properties (max of 33 items)
     * @throws IllegalArgumentException If properties contains more than 33 items
     */
    public void saveAll(List<Property> properties) throws IllegalArgumentException {
        // transactionWrite() uses transactionWriteItems().
        // transactionWriteItems() accepts a maximum of 100 operations per transaction.
        // Each property here may consume at most 3 operations each, depending on
        // the situation. Therefore, a max limit of 100/3 = 33 items per method call.
        // https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/dynamodbv2/datamodeling/DynamoDBMapper.html#transactionWrite-com.amazonaws.services.dynamodbv2.datamodeling.TransactionWriteRequest-com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig-
        if (properties.size() > 33)
            throw new IllegalArgumentException("Max limit of 33 items exceeded.");

        TransactionWriteRequest writeRequest = new TransactionWriteRequest();

        // constraint that id cannot be duplicate
        DynamoDBTransactionWriteExpression ifKeyDoesNotExist =
                new DynamoDBTransactionWriteExpression()
                        .withConditionExpression("attribute_not_exists(id)");

        for (Property property : properties) {

            Property existing = property.getId() == null
                    ? null
                    : MAPPER.load(property);

            Property newKey = uniqueKey(property);

            if (existing == null) {
                // this is a brand-new property; put property and new unique key entry
                writeRequest
                        .addPut(newKey, ifKeyDoesNotExist)
                        .addPut(property);
            } else {
                // property already exists in the database; in order to preserve any
                // additional fields that may not exist outside this database, use
                // update rather than put
                writeRequest.addUpdate(property);

                // evaluate if existing unique key entry needs to be replaced
                Property oldKey = uniqueKey(existing);

                if (!oldKey.equals(newKey)) {
                    // unique key entry has changed; replace old entry with new
                    writeRequest
                            .addDelete(oldKey)
                            .addPut(newKey);

                } // else -> unique key entry remains the same; no action needed
            }
        }
        try {
            MAPPER.transactionWrite(writeRequest);

        } catch (TransactionCanceledException e) {
            String error;

            if (properties.size() == 1) {
                Property property = properties.get(0);
                error = String.format("Unique constraint violation: hostId=%s, name=%s is not unique.",
                        property.getHostId(),
                        property.getName());
            } else {
                error = "Unique constraint violation: hostId + name is not unique for at least one entry.";
            }
            throw new TransactionCanceledException(error);
        }
    }

    /**
     * Deletes the properties with the given ids
     *
     * @param ids A list of property ids (max of 25 items)
     * @throws IllegalArgumentException If ids contains more than 25 items
     */
    public void deleteAll(List<String> ids) throws IllegalArgumentException {
        // batchDelete() calls batchWriteItems().
        // batchWriteItems() accepts a maximum of 25 items per request.
        // A common workaround is to call batchDelete in a loop until all items
        // are processed, but I have decided to just enforce a limit here
        // https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/dynamodbv2/datamodeling/AbstractDynamoDBMapper.html#batchDelete-java.lang.Iterable-
        if (ids.size() > 25)
            throw new IllegalArgumentException("Max limit of 25 items exceeded.");

        List<Property> properties = ids.stream().map(this::findOne).toList();
        List<Property> uniqueKeys = properties.stream().map(this::uniqueKey).toList();
        MAPPER.batchDelete(properties);
        MAPPER.batchDelete(uniqueKeys);
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
}
