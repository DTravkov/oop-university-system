package model.domain;

import java.util.Date;
import java.util.Objects;

import utils.FieldValidator;

public class Message extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private int receiverId;
    private int senderId;
    private final String content;
    private final Date sentDate;


    public Message(int senderId, int receiverId, String content) {
        FieldValidator.requirePositive(senderId, "Sender id");
        FieldValidator.requirePositive(receiverId, "Receiver id");
        FieldValidator.requireNonBlank(content, "Content");

        this.senderId = senderId;
        this.receiverId = receiverId;

        this.content = content;
        this.sentDate = new Date();
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
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
        return receiverId == message.receiverId && senderId == message.senderId && Objects.equals(content, message.content) && Objects.equals(sentDate, message.sentDate);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(receiverId, senderId, content, sentDate);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from=" + senderId +
                ", to=" + receiverId +
                ", content='" + content + '\'' +
                '}';
    }
}
