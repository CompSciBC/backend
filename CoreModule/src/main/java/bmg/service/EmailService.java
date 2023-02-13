package bmg.service;

import bmg.dto.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Objects;

/**
 * Provides email messaging services
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender MAILER;
    private final TemplateEngine ENGINE;

    /**
     * Sends an email message using the specified template and model
     *
     * @param email An email
     * @param templateName The name of a Thymeleaf template
     * @param model A map of key/value pairs to populate the template
     * @throws MessagingException If an error occurs during message transmission
     */
    public void sendMessage(Email email, String templateName, Map<String, Object> model) throws MessagingException {
        Context context = new Context();
        context.setVariables(model);
        String html = ENGINE.process(templateName, context);

        MimeMessage message = MAILER.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(email.getFrom());
        helper.setTo(email.getTo());
        helper.setCc(Objects.requireNonNullElse(email.getCc(), new String[]{}));
        helper.setBcc(Objects.requireNonNullElse(email.getBcc(), new String[]{}));
        helper.setSubject(email.getSubject());
        helper.setText(html, true);

        MAILER.send(message);
    }
}
