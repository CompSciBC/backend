package bmg.service;

import bmg.model.*;
import bmg.repository.ChatRepository;
import bmg.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/** provides service for Chats objects*/

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ReservationService reservationService;

    /** save a message in Chat table
     *
     * @param message
     */
    public void saveChatMessage(Message message) {
        chatRepository.saveMessageChat(convertMessageToMessageDBRecord(message));
    }

    /** save a message from a Private chat in Inbox table
     *  for both receiver and sender
     * @param message
     */

    public void saveChatPrivateMessageInbox(Message message) {
        chatRepository.saveMessageInbox(convertMessageToInboxDBRecordForUser(message));
        chatRepository.saveMessageInbox(convertMessageToInboxDBRecordForReceiver(message));
    }

    /** save a message from a Group chat in Inbox table
     *  for both all users under a certain reservation
     * @param message
     */

    public void saveChatPublicMessageInbox(Message message) {
        String reservationId = message.getReservationId();
        List<String> userIdList = getUserIdWithTheSameReservationId(reservationId);

        //save a message into a host inbox. A group message has a hostId as a receiverId
        chatRepository.saveMessageInbox(convertMessageToInboxDBRecordForReceiver(message));


        //save a message into all guests inbox
        for (int i = 0; i < userIdList.size(); i++){
            message.setReceiverId(userIdList.get(i));
            chatRepository.saveMessageInbox(convertMessageToInboxDBRecordForReceiver(message));
        }
    }


    /**
     * load chat by a given reservationId. Host perspective
     */
    public Map<String, List<Message>> loadChatMessagesForHost(String reservationId) {
        if (reservationId == null) {
            throw new NoSuchElementException("There is no chat room if reservationId is unknown");
        }
        List<MessageDBRecord> allChat = chatRepository.retrieveMessageForGivenReservationId(reservationId);
        HashMap<String, List<Message>> chatMap = new HashMap<>();

        // Always create a message array for the group chat.
        chatMap.put(reservationId, new ArrayList<Message>());

        // TODO: Create a message array for each registered guest.

        for (int i = 0; i < allChat.size(); i++) {
            String key = allChat.get(i).getChatId();
            Message message = convertMessageDBRecordToMessage(allChat.get(i));

            List<Message> messages = null;

            if (chatMap.containsKey(key)) {
                messages = chatMap.get(key);
            } else {
                messages = new ArrayList<Message>();
                chatMap.put(key, messages);
            }
            messages.add(message);
        }
        return chatMap;
    }

    /**
     * load chat by a given reservationId and a chatID
     */
    public Map<String, List<Message>> loadChatMessagesForGuest(String reservationId, String guestId) {
        String groupChatId = reservationId;
        String hostChatId = reservationId + "_" + guestId;
        List<MessageDBRecord> groupChat = this.chatRepository.retrieveMessageForGivenChatId(groupChatId);
        List<MessageDBRecord> hostChat = this.chatRepository.retrieveMessageForGivenChatId(hostChatId);
        HashMap<String, List<Message>> result = new HashMap<>();

        List<Message> groupResult = new ArrayList<>();
        for (int i = 0; i < groupChat.size(); i++) {
            Message groupMessage = convertMessageDBRecordToMessage(groupChat.get(i));
            groupResult.add(groupMessage);
        }
        result.put(groupChatId, groupResult);

        List<Message> hostResult = new ArrayList<>();
        for (int i = 0; i < hostChat.size(); i++) {
            Message hostMessage = convertMessageDBRecordToMessage(hostChat.get(i));
            hostResult.add(hostMessage);
        }
        result.put(hostChatId, hostResult);

        return result;
    }

    /**
     * load the last two messages by given reservationID
     */
    public List<Message> loadLatestMessagesByGivenReservationID(String reservationId) {
        List<MessageDBRecord> DBmessageResult = chatRepository.retrieveLatestMessageForGivenReservation(reservationId);
        List<Message> result = new ArrayList<>();
        List<Message> groupChatResult = new ArrayList<>();

        if (DBmessageResult.size() == 0) {
            return result;
        }
        if (DBmessageResult.size() == 1) {
            Message message = convertMessageDBRecordToMessage(DBmessageResult.get(0));
            result.add(message);
            return result;
        }
//in case of a long chat history; retrieve only two last messages from Group Chat
        // TODO: Probably, Private Chat can be considered as well.
        if (DBmessageResult.size() >= 2) {

            for (int i = 0; i < DBmessageResult.size(); i++) {
                Message message = convertMessageDBRecordToMessage(DBmessageResult.get(i));
                if (message.getReceiverName() == null) {
                    groupChatResult.add(message);
                }
            }
        }
        int firstMessage = groupChatResult.size()-2;
        int secondMessage = groupChatResult.size()-1;

        result.add(groupChatResult.get(firstMessage));
        result.add(groupChatResult.get(secondMessage));

        return result;

    }


    /**
     * load inbox messages for a user. The final list of Inbox messages are set by ChatID.
     * Each ChatId is responsible for a conversation. ChatId represents both private and group chats
     */

    public Map<String, List<Message>> loadInboxMessagesForUser (String userId){
        Map<String, List<Message>> result = new HashMap<>();
        List<InboxDBRecord> listInboxDBRecord = chatRepository.retrieveMessageForGivenUserId(userId);


        for (int i = 0; i < listInboxDBRecord.size(); i++){
            Message message = convertInboxDBRecordToMessage(listInboxDBRecord.get(i));
            String chatId = listInboxDBRecord.get(i).getChatId();

            if (result.containsKey(chatId)){
                result.get(chatId).add(message);
            }
            else {
                List<Message> messageList = new ArrayList<>();
                messageList.add(message);
                result.put(chatId, messageList);
            }

        }
        return result;
    }

    /** get a userId by a username to save a private message for both a sender and a receiver inboxes
    */

    private String getUserIdByUsername (String username){
        User user = chatRepository.findUsersByUserName(username);
        String userId = user.getUserID();
        return userId;

    }
    /**get a list of users with the same reservationId aka ChatId to save a group message for all users' inboxes
     In Group Chat ChatId ia the same as the ReservationId
     */
    private List<String> getUserIdWithTheSameReservationId (String reservationId) {
        List<Reservation> reservationList = chatRepository.findListOfUsersByReservationId(reservationId);
        List<String> userIdList = new ArrayList<>();

        for (int i = 0; i < reservationList.size(); i++){
            userIdList.add(reservationList.get(i).getId());
        }
        return userIdList;
    }






    private MessageDBRecord convertMessageToMessageDBRecord (Message message){
        MessageDBRecord record = new MessageDBRecord();
        record.setChatId(message.getChatId());
        record.setReservationId(message.getReservationId());
        record.setSenderName(message.getSenderName());
        record.setMessage(message.getMessage());
        record.setTimestamp(message.getTimestamp());

        return record;
        }

    private Message convertMessageDBRecordToMessage (MessageDBRecord record){
        Message message = new Message();
        message.setMessage(record.getMessage());
        message.setTimestamp(record.getTimestamp());
        message.setSenderName(record.getSenderName());
        message.setReservationId(record.getReservationId());
        message.setReceiverName(null);
        message.setChatId(record.getChatId());

        if (!record.getReservationId().equals(record.getChatId()) ){
            String [] arrayToSplit = record.getChatId().split("_");
            String receiverName = arrayToSplit[1];
            message.setReceiverName(receiverName);
        }
        return message;
    }

    private InboxDBRecord convertMessageToInboxDBRecordForUser(Message message){
        InboxDBRecord record = new InboxDBRecord();
        record.setChatId(message.getChatId());
        record.setReservationId(message.getReservationId());
        record.setSenderName(message.getSenderName());
        record.setMessage(message.getMessage());
        record.setTimestamp(message.getTimestamp());
        record.setUserId(message.getUserId());
        return record;
    }

    private InboxDBRecord convertMessageToInboxDBRecordForReceiver(Message message){
        InboxDBRecord record = new InboxDBRecord();
        record.setChatId(message.getChatId());
        record.setReservationId(message.getReservationId());
        record.setSenderName(message.getSenderName());

        record.setMessage(message.getMessage());
        record.setTimestamp(message.getTimestamp());

        // a message in private chat from a guest to a host
        if (message.getReceiverId() != null){
            record.setUserId(message.getReceiverId());
        }
        // a message in private chat from a host to a guest
        if (message.getReceiverName() != null){
            String userIdReceiver = getUserIdByUsername(message.getReceiverName());
            record.setUserId(userIdReceiver);
        }

        return record;
    }

    private Message convertInboxDBRecordToMessage (InboxDBRecord record){
        Message message = new Message();
        message.setMessage(record.getMessage());
        message.setTimestamp(record.getTimestamp());
        message.setSenderName(record.getSenderName());
        message.setReservationId(record.getReservationId());
        message.setReceiverName(null);
        message.setChatId(record.getChatId());

        if (!record.getReservationId().equals(record.getChatId()) ){
            String [] arrayToSplit = record.getChatId().split("_");
            String receiverName = arrayToSplit[1];
            message.setReceiverName(receiverName);
        }
        return message;
    }






 }





