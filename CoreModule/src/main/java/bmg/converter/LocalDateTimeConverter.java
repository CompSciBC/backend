package bmg.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Converts local date times to long, and vice versa
 */
public class LocalDateTimeConverter implements DynamoDBTypeConverter<Long, LocalDateTime> {
    @Override
    public Long convert(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public LocalDateTime unconvert(Long dateTime) {
        return LocalDateTime.ofEpochSecond(dateTime, 0, ZoneOffset.UTC);
    }
}
