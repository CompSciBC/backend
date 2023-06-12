package bmg.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the width and height of a rectangle
 */
@Getter
@RequiredArgsConstructor
public class Dimensions {
    private final Double WIDTH;
    private final Double HEIGHT;

    @Override
    public String toString() {
        return WIDTH + "x" + HEIGHT;
    }
}
