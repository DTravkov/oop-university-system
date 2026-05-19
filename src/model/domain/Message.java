package model.domain;

import java.util.Date;

import utils.FieldValidator;

public class Message extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private Employee sender;
    private final String content;
    private final Date sentDate;

    public Message(Employee sender,  String content) {
        FieldValidator.requireNonNull(sender);
        FieldValidator.requireNonBlank(content);
        this.sender = sender;
        this.content = content;
        this.sentDate = new Date();
    }

    public Employee getSender() {
        return sender;
    }

    public void setSender(Employee sender) {
        FieldValidator.requireNonNull(sender);
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public Date getSentDate() {
        return sentDate;
    }

    @Override
    public String asLine() {
        return String.format("ID: %d | From: %s | %s", id, sender.getFullname(), content);
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append('\n');
        sb.append("Sender:\n").append(sender.asLine()).append('\n');
        sb.append("Sent: ").append(sentDate).append('\n');
        sb.append("Content:\n").append(content).append('\n');
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from=" + sender +
                ", content='" + content + '\'' +
                '}';
    }
}
