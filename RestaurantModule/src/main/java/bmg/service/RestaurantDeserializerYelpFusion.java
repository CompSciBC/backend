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
 * Deserializes response object from Yelp Fusion into a {@link Restaurant}
 */
public class RestaurantDeserializerYelpFusion extends StdDeserializer<Restaurant> {
    public RestaurantDeserializerYelpFusion() {
        super(Restaurant.class);
    }

    @Override
    public Restaurant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        return Restaurant.builder()
                .id(node.at("/id").asText())
                .alias(node.at("/alias").asText())
                .name(node.at("/name").asText())
                .imageUrl(node.at("/image_url").asText())
                .isOpen(!node.at("/is_closed").asBoolean())
                .url(node.at("/url").asText())
                .numReviews(node.at("/review_count").asInt())
                .categories(StreamSupport
                        .stream(node.at("/categories").spliterator(), false)
                        .map((cat) -> cat.at("/title").asText())
                        .toArray(String[]::new))
                .rating(node.at("/rating").asDouble())
                .coordinates(new Coordinates(
                        node.at("/coordinates/latitude").asDouble(),
                        node.at("/coordinates/longitude").asDouble()))
                .transactions(StreamSupport
                        .stream(node.at("/transactions").spliterator(), false)
                        .map(JsonNode::asText)
                        .toArray(String[]::new))
                .price(node.at("/price").asText().length() + 0.0)
                .address(new Address(
                        node.at("/location/address1").asText(),
                        node.at("/location/address2").asText(),
                        node.at("/location/city").asText(),
                        node.at("/location/state").asText(),
                        node.at("/location/zip_code").asText(),
                        node.at("/location/country").asText()))
                .phone(node.at("/phone").asText())
                .displayPhone(node.at("/display_phone").asText())
                .distance(node.at("/distance").asDouble())
                .build();
    }
}
