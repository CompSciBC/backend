package bmg.dto;

import lombok.Getter;

/**
 * Represents an invitation to join a reservation
 */
@Getter
public class Invitation {
    private String[] recipients;
    private String guestName;
    private String message;
}
