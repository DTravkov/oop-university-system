package model;

public class MessageRepository extends MapRepository<Message> {

    @Override
    protected String getFilePath() {
        return "messages.ser";
    }
}
