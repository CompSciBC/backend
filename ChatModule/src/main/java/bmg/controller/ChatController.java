package bmg.controller;


import bmg.service.ChatService;
import bmg.model.Message;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final List<Message> messages;
    private final ChatService chatService;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate, ChatService chatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
        this.messages = new ArrayList<Message>();
    }

    @MessageMapping("/private-message")
    public Message receivePrivateMessage (@Payload Message message){
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        message.setTimestamp(currentTime.getTime());
        this.messages.add(message);
        this.chatService.saveChatMessage(message);

        String destination = "/private/" + message.getReservationId();
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), destination, message);
        return message;
    }

    @MessageMapping("/group-message")
    public Message receiveGroupMessage (@Payload Message message){
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        message.setTimestamp(currentTime.getTime());
        this.messages.add(message);
        this.chatService.saveChatMessage(message);
        simpMessagingTemplate.convertAndSend("/group/" + message.getReservationId(), message);
        return message;
    }
    @GetMapping("/load/host/{reservationId}")
    public Map<String, List<Message>> retrieveMessagesHost (
            @PathVariable(name = "reservationId") String reservationID
            ){
        return chatService.loadChatMessagesForHost(reservationID);

    }
    @GetMapping("/load/guest/{reservationId}/{guestId}")
    public Map<String, List<Message>> retrieveMessagesGuest (
            @PathVariable(name = "reservationId")String reservationId,
            @PathVariable(name = "guestId") String guestId
    ){
       return chatService.loadChatMessagesForGuest(reservationId, guestId);

    }








}