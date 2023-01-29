package bmg.controller;

import bmg.dto.Email;
import bmg.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles requests for {@link Email} messages
 */
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController extends Controller<String> {

    private final EmailService SVC;

    /**
     * Sends an email message
     *
     * @param email An email
     * @return A response entity containing the result of the email transaction
     */
    @PostMapping("/send")
    public ResponseEntity<Response<String>> send(@RequestBody Email email) {
        try {
            SVC.sendMessage(email);
            return responseCodeOk(List.of("Email sent successfully."));

        } catch (MailSendException e) {
            String error = e.getMessage();
            error = error != null ? error : "Failed to send email.";
            return responseCodeBadRequest(error);
        }
    }
}
