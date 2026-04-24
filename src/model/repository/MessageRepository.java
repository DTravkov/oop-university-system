package model.repository;

import model.domain.Message;

import java.util.Collection;


public class MessageRepository extends Repository<Message> {

    private static final MessageRepository INSTANCE = new MessageRepository();

    private MessageRepository() {
        super();
    }

    public static MessageRepository getInstance() {
        return INSTANCE;
    }

    public Collection<Message> findAllBySenderId(int senderId){
        return this.data.values().stream()
                .filter(entity -> entity.getSenderId() == senderId)
                .toList();
    }

    public Collection<Message> findAllByReceiverId(int receiverId){
        return this.data.values().stream()
                .filter(entity -> entity.getReceiverId() == receiverId)
                .toList();
    }

}
