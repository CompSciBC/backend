package bmg.service;


import bmg.dto.Coordinates;
import bmg.dto.Place;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.stream.StreamSupport;

public class PlaceDeserializer extends StdDeserializer<Place> {

    @Override
    public Place deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        return Place.builder()
                .name(node.at("/name").asText())
                .rating(node.at("/rating").asDouble())
                .loc(new Coordinates(
                        node.at("/geometry/location/lat").asDouble(),
                        node.at("/geometry/location/lng").asDouble()))
                .types(StreamSupport
                        .stream(node.at("/types").spliterator(), false)
                        .map(JsonNode::asText)
                        .toArray(String[]::new))
                .vicinity(node.at("/vicinity").asText())
                .priceLvl(node.at("/price_level").asInt())
                .openNow(node.at("/opening_hours/open_now").asBoolean())
                .placeID(node.at("/place_id").asText())
                .userPhotoReference(node.at("/photos/0/photo_reference").asText())
                .build();
    }

    protected PlaceDeserializer() {
        super(Place.class);
    }
}