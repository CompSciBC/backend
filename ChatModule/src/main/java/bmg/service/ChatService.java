package bmg.service;

import bmg.model.Message;
import bmg.model.MessageDBRecord;
import bmg.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/** provides service for Chats objects*/

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public void saveChatMessage(Message message) {
        chatRepository.saveMessage(convertMessageToMessageDBRecord(message));
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




 }





