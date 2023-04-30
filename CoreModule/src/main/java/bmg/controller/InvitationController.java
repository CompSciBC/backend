package bmg.controller;

import bmg.dto.Invitation;
import bmg.service.InvitationService;
import bmg.service.QRCodeService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles requests related to {@link Invitation}s and QR codes
 */
@RestController
@CrossOrigin
@RequestMapping("/api/invites")
@RequiredArgsConstructor
@Log4j2
public class InvitationController extends Controller<Object> {

    private final InvitationService I_SVC;
    private final QRCodeService Q_SVC;

    /**
     * Gets a temporary URL to access the invite QR code with the given id
     *
     * @param id A QR code id
     * @return A response entity containing a URL to the QR code
     */
    @GetMapping("/{id}/qr-code")
    public ResponseEntity<Response<Object>> getQRCodeURL(@PathVariable(name = "id") String id) {
        log.info("Get QR code URL for reservationId={}", id);
        return responseCodeOk(List.of(Q_SVC.getURL(id)));
    }

    /**
     * Sends an invitation email
     *
     * @param id A reservation id
     * @param invitation An invitation
     * @return A response entity containing the result of the email transaction
     */
    @PostMapping("/{id}/send-email")
    public ResponseEntity<Response<Object>> sendEmail(@PathVariable(name = "id") String id,
                                                      @RequestBody Invitation invitation) {

        log.info("Send email invitation for reservationId={}:", id);
        log.info("\tmessage=\"{}\"", invitation.getMessage());
        log.info("\tRecipients:");

        for (String recipient : invitation.getRecipients())
            log.info("\t\t{}", recipient.trim());

        try {
            I_SVC.sendInvites(id, invitation);
            return responseCodeOk(List.of("Invitation sent successfully."));

        } catch (MailSendException | MessagingException e) {
            String error = e.getMessage();
            error = error != null ? error : "Failed to send invitation.";
            log.error("\t{}", error);
            return responseCodeBadRequest(error);
        }
    }
}
