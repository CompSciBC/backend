package bmg.service;

import bmg.dto.Email;
import bmg.dto.Invitation;
import bmg.model.Property;
import bmg.model.Reservation;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final EmailService SVC;
    private final ReservationService R_SVC;
    private final PropertyService P_SVC;

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
        Reservation reservation = R_SVC.findOne(id);

        if (reservation == null)
            throw new NoSuchElementException("Reservation id="+id+" does not exist.");

        Property property = P_SVC.findOne(reservation.getPropertyId());

        Map<String, Object> model = new HashMap<>();
        model.put("invitee", "friend");
        model.put("propertyName", property.getName());

        String name = invite.getGuestName();
        model.put("invitor", name.equals("") ? "your friend" : name);

        model.put("checkIn", reservation.getCheckIn().format(DateTimeFormatter.ofPattern("EEEE M/d/yy")));
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
