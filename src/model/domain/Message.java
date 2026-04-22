package model.domain;

import java.util.Date;
import java.util.Objects;

import utils.FieldValidator;

public class Message extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private int receiverId;
    private int senderId;
    private String content;
    private Date sentDate;


    public Message(int senderId, int receiverId, String content) {
        FieldValidator validator = new FieldValidator()
                .requirePositive(senderId, "Sender id")
                .requirePositive(receiverId, "Receiver id")
                .requireNonBlank(content, "Content");
        validator.validate();

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

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        if(this.id != 0 && message.getId() != 0) return this.id == message.getId();
        return receiverId == message.receiverId && senderId == message.senderId && Objects.equals(content, message.content) && Objects.equals(sentDate, message.sentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiverId, senderId, content, sentDate);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                "senderId=" + senderId +
                ", content='" + content + '\'' +
                '}';
    }
}
