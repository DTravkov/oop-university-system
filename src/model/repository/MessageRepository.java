package model.repository;

import model.domain.Message;

import java.util.List;


public class MessageRepository extends Repository<Message> {

    private static final MessageRepository INSTANCE = new MessageRepository();

    private MessageRepository() {
        super();
    }

    public static MessageRepository getInstance() {
        return INSTANCE;
    }

    public List<Message> findAllBySenderId(int senderId) {
        return this.findAll(entity -> entity.getSender().getId() == senderId);
    }

    public List<Message> findAllByReceiverId(int receiverId) {
        return this.findAll(entity -> entity.getReceiver().getId() == receiverId);
    }

}
