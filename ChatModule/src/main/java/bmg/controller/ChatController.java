package bmg.controller;


import bmg.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;




@Controller
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController (SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/private-message")
    public Message receivePrivateMessage (@Payload Message message){
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName()+message.getReservationID(), "/private", message);
        return message;
    }








}