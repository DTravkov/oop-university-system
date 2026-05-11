package model.domain;

import java.util.Date;
import java.util.Objects;

import utils.FieldValidator;

public class Message extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private Employee sender;
    private final String content;
    private final Date sentDate;

    public Message(Employee sender,  String content) {
        FieldValidator.requireNonNull(sender, "Sender");
        FieldValidator.requireNonBlank(content, "Content");
        this.sender = sender;
        this.content = content;
        this.sentDate = new Date();
    }

    public Employee getSender() {
        return sender;
    }

    public void setSender(Employee sender) {
        FieldValidator.requireNonNull(sender, "Sender");
        this.sender = sender;
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
                && Objects.equals(content, message.content)
                && Objects.equals(sentDate, message.sentDate);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(sender, content, sentDate);
    }

    @Override
    public String asLine() {
        String preview = content.length() > 40 ? content.substring(0, 37) + "..." : content;
        return String.format("ID: %d | From: %s | %s", id, sender.getFullname(), preview);
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
