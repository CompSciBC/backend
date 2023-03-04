package bmg.model;

import lombok.*;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
/**
 * Represents a chat...
 */

public class Message {
    private String reservationId;
    private Long timestamp;
    private String senderName;
    private String receiverName;
    private String message;
    private String chatId;

}


