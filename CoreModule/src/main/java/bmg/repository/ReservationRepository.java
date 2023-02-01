package bmg.repository;

import bmg.converter.LocalDateTimeConverter;
import bmg.model.Reservation;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTransactionWriteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.TransactionWriteRequest;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.TransactionCanceledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Provides CRUD operations for {@link Reservation} objects
 */
@Repository
@RequiredArgsConstructor
public class ReservationRepository {

    private final DynamoDBMapper MAPPER;

    /**
     * Finds all reservations with the given id
     *
     * @param id A reservation id
     * @param primaryOnly If true, returns only the primary reservation entry
     * @return A list of reservations
     */
    public List<Reservation> findAll(String id, boolean primaryOnly) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":v1", new AttributeValue().withS(id));

        String keys = "id = :v1";
        String filters = setFilters(null, values, primaryOnly);

        return findAllByCondition(null, keys, filters, values);
    }

    /**
     * Finds all reservations by the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param primaryOnly If true, returns only primary reservation entries
     * @return A list of reservations
     */
    public List<Reservation> findAll(Reservation.Index index, String id, boolean primaryOnly) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":v1", new AttributeValue().withS(id));

        String keys = String.format("%s = :v1", index.getKEY());
        String filters = setFilters(null, values, primaryOnly);

        return findAllByCondition(index.getNAME(), keys, filters, values);
    }

    /**
     * Finds all reservations with the given id and a check-in date/time after the cutoff
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param primaryOnly If true, returns only primary reservation entries
     * @param cutoff A date/time (exclusive)
     * @return A list of reservations
     */
    public List<Reservation> findAllCheckInAfter(Reservation.Index index,
                                                 String id,
                                                 boolean primaryOnly,
                                                 LocalDateTime cutoff) {

        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":v1", new AttributeValue().withS(id));
        values.put(":v2", new AttributeValue().withN(convertDate(cutoff)));

        String keys = String.format("%s = :v1 AND checkIn > :v2", index.getKEY());
        String filters = setFilters(null, values, primaryOnly);

        return findAllByCondition(index.getNAME(), keys, filters, values);
    }

    /**
     * Finds all reservations with the given id and a check-out date/time before the cutoff
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param primaryOnly If true, returns only primary reservation entries
     * @param cutoff A date/time (exclusive)
     * @return A list of reservations
     */
    public List<Reservation> findAllCheckOutBefore(Reservation.Index index,
                                                   String id,
                                                   boolean primaryOnly,
                                                   LocalDateTime cutoff) {

        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":v1", new AttributeValue().withS(id));
        values.put(":v2", new AttributeValue().withN(convertDate(cutoff)));

        String keys = String.format("%s = :v1", index.getKEY());
        String filters = setFilters("checkOut < :v2", values, primaryOnly);

        return findAllByCondition(index.getNAME(), keys, filters, values);
    }

    /**
     * Finds all reservations with the given id and check-in/out date/time between
     * the given cutoff date/times (checkIn <= checkInCutoff & checkOut > checkOutCutoff)
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param primaryOnly If true, returns only primary reservation entries
     * @param checkInCutoff A date/time (inclusive)
     * @param checkOutCutoff A date/time (exclusive)
     * @return A list of reservations
     */
    public List<Reservation> findAllCheckInOnOrBeforeCheckOutAfter(Reservation.Index index,
                                                                   String id,
                                                                   boolean primaryOnly,
                                                                   LocalDateTime checkInCutoff,
                                                                   LocalDateTime checkOutCutoff) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":v1", new AttributeValue().withS(id));
        values.put(":v2", new AttributeValue().withN(convertDate(checkInCutoff)));
        values.put(":v3", new AttributeValue().withN(convertDate(checkOutCutoff)));

        String keys = String.format("%s = :v1 AND checkIn <= :v2", index.getKEY());
        String filters = setFilters("checkOut > :v3", values, primaryOnly);

        return findAllByCondition(index.getNAME(), keys, filters, values);
    }

    /**
     * Executes a query with the given parameters
     *
     * @param indexName The name of the reservation index
     * @param keyExpression An expression involving key attributes (hash/range)
     * @param filterExpression An expression involving non-key attributes
     * @param values A map of attribute/value pairs
     * @return A list of reservations
     */
    private List<Reservation> findAllByCondition(String indexName,
                                                 String keyExpression,
                                                 String filterExpression,
                                                 Map<String, AttributeValue> values) {
        return MAPPER.query(
                Reservation.class,
                new DynamoDBQueryExpression<Reservation>()
                        .withIndexName(indexName)
                        .withKeyConditionExpression(keyExpression)
                        .withFilterExpression(filterExpression)
                        .withExpressionAttributeValues(values)
                        .withConsistentRead(false));
    }

    /**
     * Converts a date/time to a string
     *
     * @param datetime A date/time
     * @return A string representation of dateTime
     */
    private String convertDate(LocalDateTime datetime) {
        return new LocalDateTimeConverter().convert(datetime).toString();
    }

    /**
     * Adds an "isPrimary = true" filter to the given values map if primaryOnly is true
     * and appends the expression to the given filter expression; otherwise returns the
     * given filter expression
     *
     * @param filterExpression A filter expression
     * @param values A map of attribute/value pairs
     * @param primaryOnly If true, returns only primary reservation entries
     * @return A filter expression string for isPrimary
     */
    private String setFilters(String filterExpression,
                              Map<String, AttributeValue> values,
                              boolean primaryOnly) {
        if (primaryOnly) {
            // add filter for primary
            values.put(":vPrimary", new AttributeValue().withBOOL(true));
            String primaryFilter = "isPrimary = :vPrimary";

            return filterExpression == null
                    ? primaryFilter
                    : filterExpression + " AND " + primaryFilter;
        } else {
            // do not add filter for primary
            return filterExpression;
        }
    }

    /**
     * Saves the given reservation
     *
     * @param reservation A reservation
     */
    public void saveOne(Reservation reservation) {
        String id = reservation.getId();
        boolean idDoesNotYetExist = id == null || findAll(id, false).size() == 0;

        if (idDoesNotYetExist) {
            // this is the primary reservation
            reservation.setIsPrimary(true);
            MAPPER.save(reservation);

        } else {
            // reservation is a copy/non-primary. Ensure that it is not a duplicate entry.
            // Only one instance of an id + guestId combo may exist in the database
            reservation.setIsPrimary(false);

            // checks that the combination of id + guestId does not already exist in the database
            DynamoDBTransactionWriteExpression ifKeysDoNotExist =
                    new DynamoDBTransactionWriteExpression()
                            .withConditionExpression(
                                    "attribute_not_exists(id) AND attribute_not_exists(guestId)");
            try {
                MAPPER.transactionWrite(
                        new TransactionWriteRequest()
                                .addPut(reservation, ifKeysDoNotExist));

            } catch (TransactionCanceledException e) {
                throw new TransactionCanceledException(
                        String.format("Duplicate entry for id=%s, guestId=%s.",
                                id,
                                reservation.getGuestId()));
            }
        }
    }

    /**
     * Updates an existing reservation with the given updates
     *
     * @param updatedReservation A reservation
     */
    public void updateOne(Reservation updatedReservation) {
        boolean reservationExists = MAPPER.load(updatedReservation) != null;

        if (reservationExists) {
            // save only if id + guestId combo exists in the database
            MAPPER.save(updatedReservation);

        } else {
            // id + guestId combo does not exist
            // use #saveOne(Reservation) to save a new reservation instead
            throw new NoSuchElementException(
                    String.format("Reservation with id=%s, guestId=%s does not exist.",
                            updatedReservation.getId(),
                            updatedReservation.getGuestId()));
        }
    }

    /**
     * Deletes all reservations with the given id
     *
     * @param id A reservation id
     */
    public void deleteAll(String id) {
        MAPPER.batchDelete(findAll(id, false));
    }
}
