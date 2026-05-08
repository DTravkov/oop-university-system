package model.repository;

import model.domain.Message;


public class MessageRepository extends Repository<Message> {

    private static final MessageRepository INSTANCE = new MessageRepository();

    private MessageRepository() {
        super();
    }

    public static MessageRepository getInstance() {
        return INSTANCE;
    }

}
