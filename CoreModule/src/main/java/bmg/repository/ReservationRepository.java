package bmg.repository;

import bmg.converter.LocalDateTimeConverter;
import bmg.model.Reservation;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides CRUD operations for {@link Reservation} objects
 */
@Repository
@RequiredArgsConstructor
public class ReservationRepository {

    private final DynamoDBMapper MAPPER;

    /**
     * Finds the reservation with the given id
     *
     * @param id A reservation id
     * @return The reservation with the given id
     */
    public Reservation findOne(String id) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        return MAPPER.load(reservation);
    }

    /**
     * Finds all reservations by the given index and id
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @return A list of reservations
     */
    public List<Reservation> findAll(Reservation.Index index, String id) {
        Reservation reservation = new Reservation();

        switch (index) {
            case HOST -> reservation.setHostId(id);
            case PROPERTY -> reservation.setPropertyId(id);
            case GUEST -> reservation.setGuestId(id);
            default -> {}
        }
        return MAPPER.query(
                Reservation.class,
                new DynamoDBQueryExpression<Reservation>()
                        .withHashKeyValues(reservation)
                        .withIndexName(index.getNAME())
                        .withConsistentRead(false));
    }

    /**
     * Finds all reservations with the given id and check in after the cutoff
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param cutoff A date/time (exclusive)
     * @return A list of reservations
     */
    public List<Reservation> findAllCheckInAfter(Reservation.Index index, String id, LocalDateTime cutoff) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":v1", new AttributeValue().withS(id));
        values.put(":v2", new AttributeValue().withN(convertDate(cutoff)));

        return findAllByCondition(
                String.format("%s = :v1 AND checkIn > :v2", index.getKEY()),
                null,
                index.getNAME(),
                values);
    }

    /**
     * Finds all reservations with the given id and check out before the cutoff
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param cutoff A date/time (exclusive)
     * @return A list of reservations
     */
    public List<Reservation> findAllCheckOutBefore(Reservation.Index index, String id, LocalDateTime cutoff) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":v1", new AttributeValue().withS(id));
        values.put(":v2", new AttributeValue().withN(convertDate(cutoff)));

        return findAllByCondition(
                String.format("%s = :v1", index.getKEY()),
                "checkOut < :v2",
                index.getNAME(),
                values);
    }

    /**
     * Finds all reservations with the given id, between the given cutoff dates
     * (checkIn <= checkInCutoff & checkOut > checkOutCutoff)
     *
     * @param index A reservation index
     * @param id The id of a property, host, or guest
     * @param checkInCutoff A date/time (inclusive)
     * @param checkOutCutoff A date/time (exclusive)
     * @return A list of reservations
     */
    public List<Reservation> findAllCheckInOnOrBeforeCheckOutAfter(Reservation.Index index,
                                                                   String id,
                                                                   LocalDateTime checkInCutoff,
                                                                   LocalDateTime checkOutCutoff) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":v1", new AttributeValue().withS(id));
        values.put(":v2", new AttributeValue().withN(convertDate(checkInCutoff)));
        values.put(":v3", new AttributeValue().withN(convertDate(checkOutCutoff)));

        return findAllByCondition(
                String.format("%s = :v1 AND checkIn <= :v2", index.getKEY()),
                "checkOut > :v3",
                index.getNAME(),
                values);
    }

    /**
     * Executes a query with the given parameters
     *
     * @param keyExpression An expression involving only primary keys
     * @param filterExpression An expression involving non-primary keys
     * @param indexName The name of the reservation index
     * @param values A map of attribute/value pairs
     * @return A list of reservations
     */
    private List<Reservation> findAllByCondition(String keyExpression,
                                                 String filterExpression,
                                                 String indexName,
                                                 Map<String, AttributeValue> values) {
        return MAPPER.query(
                Reservation.class,
                new DynamoDBQueryExpression<Reservation>()
                        .withKeyConditionExpression(keyExpression)
                        .withFilterExpression(filterExpression)
                        .withIndexName(indexName)
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
     * Saves the given reservation
     *
     * @param reservation A reservation
     */
    public void saveOne(Reservation reservation) {
        MAPPER.save(reservation);
    }

    /**
     * Deletes the given reservation
     *
     * @param reservation A reservation
     */
    public void deleteOne(Reservation reservation) {
        MAPPER.delete(reservation);
    }
}
