package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import utils.FieldValidator;

public class Message implements Serializable, Indexed {

    private static final long serialVersionUID = 1L;

    private int id;
    private int receiverId;
    private int senderId;
    private String content;
    private Date sentDate;

    public Message(int receiverId, int senderId, String content) {
        FieldValidator validator = new FieldValidator();
        validator.requirePositive(receiverId, "Receiver ID");
        validator.requirePositive(senderId, "Sender ID");
        validator.requireNonBlank(content, "Content");
        validator.validate();

        this.receiverId = receiverId;
        this.senderId = senderId;
        this.content = content;
        this.sentDate = new Date();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
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
    public int hashCode() {
        return Objects.hash(content, id, receiverId, senderId, sentDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Message other = (Message) obj;
        return id == other.id
            && receiverId == other.receiverId
            && senderId == other.senderId
            && Objects.equals(content, other.content)
            && Objects.equals(sentDate, other.sentDate);
    }

    @Override
    public String toString() {
        return "Message[id=" + id
            + ", senderId=" + senderId
            + ", receiverId=" + receiverId
            + ", content=" + content
            + ", sentDate=" + sentDate + "]";
    }
}
