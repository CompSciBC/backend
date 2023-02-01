package bmg.controller;


import bmg.model.Message;
import bmg.repository.PrivateChatRepository;
import bmg.service.PrivateChatService;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Controller
@RestController
@RequestMapping("/api/chat")
public class PrivateChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final List<Message> messageList;
    private final PrivateChatService privateChatService;

    public PrivateChatController(SimpMessagingTemplate simpMessagingTemplate, PrivateChatService privateChatService){
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.privateChatService = privateChatService;
        this.messageList = new ArrayList<Message>();
    }

    @MessageMapping("/private-message")
    public Message receivePrivateMessage (@Payload Message message){
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        message.setTimestamp(currentTime.toString());
        this.messageList.add(message);
        this.privateChatService.savePrivateChatMessage(message);
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName()+message.getReservationId(), "/private", message);
        return message;
    }
    @GetMapping("/private-message/{reservationId}")
    public List<Message> retrievePrivateMessage (@PathVariable(name = "reservationId")String reservationID){
        return privateChatService.loadPrivateChatMessageByGivenReservationId(reservationID);

    }








}