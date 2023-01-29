package bmg.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents an email message
 */
@Getter
@Builder
public class Email {

    private String from;
    private String to;
    private String subject;
    private String body;
}
