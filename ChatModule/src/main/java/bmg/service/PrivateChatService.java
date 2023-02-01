package bmg.service;

import bmg.model.Message;
import bmg.repository.PrivateChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/** provides service for PrivateChats objects*/

@Service
@RequiredArgsConstructor
public class PrivateChatService {

    private final PrivateChatRepository chatRepository;

    /**save a private chat message into database*/

    public void savePrivateChatMessage (Message message){
        chatRepository.saveMessage(message);
    }

    /**load a list of private chat messages by a given reservationId*/
    public List<Message> loadPrivateChatMessageByGivenReservationId (String reservationId){
        if (reservationId == null){
            throw new NoSuchElementException("There is no chat room if reservationId is unknown");
        }
        return chatRepository.retrieveMessageForGivenReservationId(reservationId);
    }




}
