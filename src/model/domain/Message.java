package model.domain;

import java.util.Date;
import java.util.Objects;

import utils.FieldValidator;

public class Message extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private User sender;
    private User receiver;
    private final String content;
    private final Date sentDate;

    public Message(User sender, User receiver, String content) {
        FieldValidator.requireNonNull(sender, "Sender");
        FieldValidator.requireNonNull(receiver, "Receiver");
        FieldValidator.requireNonBlank(content, "Content");
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sentDate = new Date();
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        FieldValidator.requireNonNull(sender, "Sender");
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        FieldValidator.requireNonNull(receiver, "Receiver");
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public Date getSentDate() {
        return sentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        if (id != 0 || message.getId() != 0) {
            return id != 0 && id == message.getId();
        }
        return Objects.equals(sender, message.sender)
                && Objects.equals(receiver, message.receiver)
                && Objects.equals(content, message.content)
                && Objects.equals(sentDate, message.sentDate);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(sender, receiver, content, sentDate);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from=" + sender +
                ", to=" + receiver +
                ", content='" + content + '\'' +
                '}';
    }
}
