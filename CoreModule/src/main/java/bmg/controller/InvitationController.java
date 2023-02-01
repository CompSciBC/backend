package bmg.controller;

import bmg.dto.Invitation;
import bmg.service.InvitationService;
import bmg.service.QRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles requests related to {@link Invitation}s and QR codes
 */
@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
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
        return responseCodeOk(List.of(Q_SVC.getURL(id)));
    }

    /**
     * Sends an invitation email
     *
     * @param invitation An invitation
     * @return A response entity containing the result of the email transaction
     */
    @PostMapping("/{id}/send-email")
    public ResponseEntity<Response<Object>> sendEmail(@PathVariable(name = "id") String id,
                                                      @RequestBody Invitation invitation) {
        try {
            I_SVC.sendInvites(id, invitation);
            return responseCodeOk(List.of("Invitation sent successfully."));

        } catch (MailSendException e) {
            String error = e.getMessage();
            error = error != null ? error : "Failed to send invitation.";
            return responseCodeBadRequest(error);
        }
    }
}
