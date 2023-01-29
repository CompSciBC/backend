package bmg.service;

import bmg.dto.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Provides email messaging services
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender MAILER;

    /**
     * Sends the given email
     *
     * @param email An email
     */
    public void sendMessage(Email email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getTo());
        message.setFrom(email.getFrom());
        message.setSubject(email.getSubject());
        message.setText(email.getBody());
        MAILER.send(message);
    }
}
