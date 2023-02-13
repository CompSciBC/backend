package bmg.service;

import bmg.dto.Email;
import bmg.dto.Invitation;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
    public void sendInvites(String id, Invitation invite) throws MessagingException {
        Map<String, Object> model = new HashMap<>();
        model.put("invitee", "Joe");
        model.put("propertyName", "Yo Momma's House");
        model.put("invitor", "Matthew");
        model.put("checkIn", LocalDate.now());
        model.put("optionalMessage", invite.getMessage());
        model.put("addReservationLink", addReservationRoute + id);

        Email email = Email
                .builder()
                .from(sender)
                .to(invite.getRecipients())
                .subject("You're invited to BeMyGuest!")
                .build();

        SVC.sendMessage(email, "invite", model);
    }
}
