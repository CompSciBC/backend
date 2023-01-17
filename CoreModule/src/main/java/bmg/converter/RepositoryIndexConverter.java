package bmg.converter;

import bmg.model.Reservation;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts a string to a {@link Reservation.Index};
 * Allows controllers to receive case-insensitive index parameters
 */
@Component
public class RepositoryIndexConverter implements Converter<String, Reservation.Index> {
    @Override
    public Reservation.Index convert(String index) {
        return Reservation.Index.valueOf(index.toUpperCase());
    }
}
