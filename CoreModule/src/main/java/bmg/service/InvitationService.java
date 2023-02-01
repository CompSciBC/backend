package bmg.service;

import bmg.dto.Email;
import bmg.dto.Invitation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final EmailService SVC;

    @Value("${invitation.sender}")
    private String sender;

    @Value("${client.route.add-reservation}")
    private String addReservationRoute;

    /**
     * Sends an invitation email for the given reservation id
     *
     * @param id A reservation id
     * @param invite An invitation
     */
    public void sendInvites(String id, Invitation invite) {
        String defaultMessage = String.format("%s has invited you to join BeMyGuest.\n\n", invite.getGuestName());
        String optionalMessage = invite.getMessage();
        String body = !optionalMessage.isBlank()
                ? defaultMessage + optionalMessage + "\n\n" + addReservationRoute + id
                : defaultMessage + addReservationRoute + id;

        Email email = Email
                .builder()
                .from(sender)
                .to(invite.getRecipients())
                .subject("You're invited to BeMyGuest!")
                .body(body)
                .build();

        SVC.sendMessage(email);
    }
}
