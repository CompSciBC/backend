package bmg.controller;


import bmg.model.User;
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
@CrossOrigin
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
        this.chatService.saveChatPrivateMessageInbox(message);

        String destinationPrivateChat = "/private/" + message.getReservationId();
        String destinationInbox = "/inbox";
        if (message.getReceiverId() == null) {
            String username = message.getReceiverName();
            message.setReceiverId(chatService.getUserIdByUsername(username));
        }
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), destinationPrivateChat, message);
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), destinationInbox, message);
        simpMessagingTemplate.convertAndSendToUser(message.getUserId(), destinationInbox, message);

        return message;
    }

    @MessageMapping("/group-message")
    public Message receiveGroupMessageWebSocket(@Payload Message message) {
        return receiveGroupMessage(message);
    }

    @PostMapping("/group-message")
    public Message receiveGroupMessageHttp(@RequestBody Message message) {
        return receiveGroupMessage(message);
    }

    private Message receiveGroupMessage (Message message){
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        message.setTimestamp(currentTime.getTime());
        this.messages.add(message);
        this.chatService.saveChatMessage(message);
        this.chatService.saveChatPublicMessageInbox(message);
        simpMessagingTemplate.convertAndSend("/group/" + message.getReservationId(), message);
        List<String> userIdList = chatService.getUserIdWithTheSameReservationId(message.getReservationId());
        String destinationInbox = "/inbox";

        for (int i = 0; i < userIdList.size(); i++){
            simpMessagingTemplate.convertAndSendToUser(userIdList.get(i), destinationInbox, message);
        }
        return message;
    }

    @MessageMapping("/inbox-message")
    public Message receiveInboxMessage (@Payload Message message){
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        message.setTimestamp(currentTime.getTime());
        this.messages.add(message);
        this.chatService.saveChatMessage(message);

        String destination = "/inbox/" + message.getUserId();
        simpMessagingTemplate.convertAndSendToUser(message.getUserId(), destination, message);
        if (message.getChatId() == message.getReservationId()){
            this.chatService.saveChatMessage(message);
            simpMessagingTemplate.convertAndSend("/group/" + message.getReservationId(), message);
        }
        else {
            this.chatService.saveChatPrivateMessageInbox(message);
            String destinationPrivateChat = "/private/" + message.getReservationId();
            if (message.getReceiverId() != null) {
                simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), destinationPrivateChat, message);
            }
            else {
                String username = message.getReceiverName();
                message.setReceiverId(chatService.getUserIdByUsername(username));
                simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), destinationPrivateChat, message);
            }

        }
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
            @PathVariable(name = "reservationId") String reservationId,
            @PathVariable(name = "guestId") String guestId
    ){
       return chatService.loadChatMessagesForGuest(reservationId, guestId);

    }

    @GetMapping("/load/chat-preview/{reservationId}")
    public List<Message>retrieveChatPreviewForGuest(
            @PathVariable(name = "reservationId")String reservationId
    ){
        return chatService.loadLatestMessagesByGivenReservationID(reservationId);

    }
    @GetMapping("/load/inbox/{userId}")
    public Map<String, List<Message>> retrieveMessageInbox(
            @PathVariable(name = "userId")String userID
    ){
        return chatService.loadInboxMessagesForUser(userID);

    }








}