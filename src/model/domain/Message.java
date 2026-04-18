package model.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import utils.FieldValidator;

public class Message extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private User receiver;
    private User sender;
    private String content;
    private Date sentDate;


    public Message(User sender, User receiver, String content) {
        FieldValidator validator = new FieldValidator()
                .requireNonBlank(content, "Content");
        validator.validate();

        this.sender = sender;
        this.receiver = receiver;

        this.content = content;
        this.sentDate = new Date();
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
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
        return Objects.equals(receiver, message.receiver) && Objects.equals(sender, message.sender) && Objects.equals(content, message.content) && Objects.equals(sentDate, message.sentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiver, sender, content, sentDate);
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", content='" + content + '\'' +
                '}';
    }
}
