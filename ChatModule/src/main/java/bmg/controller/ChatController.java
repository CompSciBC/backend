package bmg.controller;


import bmg.model.Message;
import bmg.service.ChatService;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RestController
@RequestMapping("/api/chat")
public class PrivateChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final List<Message> messageList;
    private final ChatService chatService;

    public PrivateChatController(SimpMessagingTemplate simpMessagingTemplate, ChatService chatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
        this.messageList = new ArrayList<Message>();
    }

    @MessageMapping("/private-message")
    public Message receivePrivateMessage (@Payload Message message){
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        message.setTimestamp(currentTime.toString());
        this.messageList.add(message);
        this.chatService.saveChatMessage(message);
        simpMessagingTemplate.convertAndSendToUser(message.getChatId()+ message.getReservationId(), "/private", message);
        return message;
    }
    @GetMapping("/private-message/host/{reservationId}")
    public Map<String, List<Message>> retrieveMessagesHost (
            @PathVariable(name = "reservationId")String reservationID
            ){
        return chatService.loadChatMessageByGivenReservationId(reservationID);

    }

    @GetMapping("/private-message/guest/{reservationId}/{guestId}")
    public Map<String, List<Message>> retrieveMessagesGuest (
            @PathVariable(name = "reservationId")String reservationID,
            @PathVariable(name = "guestId") String guestId
    ){
        return chatService.loadChatsMessageByGivenReservationIdAndGuestId(reservationID, guestId);

    }








}