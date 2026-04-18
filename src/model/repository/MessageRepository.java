package model.repository;

import model.domain.Message;

public class MessageRepository extends MapRepository<Message> {

    @Override
    protected String getFilePath() {
        return "messages.ser";
    }
}
