package bmg.service;

import bmg.dto.Address;
import bmg.dto.Coordinates;
import bmg.dto.Restaurant;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.stream.StreamSupport;

/**
 * Deserializes response object from Google Places into a {@link Restaurant}
 */
public class RestaurantDeserializerGooglePlaces extends StdDeserializer<Restaurant> {
    protected RestaurantDeserializerGooglePlaces() {
        super(Restaurant.class);
    }

    @Override
    public Restaurant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        return Restaurant.builder()
                .id(node.at("/place_id").asText())
                .name(node.at("/name").asText())
                .isOpen(node.at("/opening_hours/open_now").asBoolean())
                .numReviews(node.at("/user_ratings_total").asInt())
                .categories(StreamSupport
                        .stream(node.at("/types").spliterator(), false)
                        .map(JsonNode::asText)
                        .toArray(String[]::new))
                .rating(node.at("/rating").asDouble())
                .coordinates(new Coordinates(
                        node.at("/geometry/location/lat").asDouble(),
                        node.at("/geometry/location/lng").asDouble()))
                .price(node.at("/price_level").asDouble())
                .address(new Address(
                        node.at("/vicinity").asText(),
                        null,
                        null,
                        null,
                        null,
                        null))
                .build();
    }
}
