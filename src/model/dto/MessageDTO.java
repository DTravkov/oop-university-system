package model.dto;

import model.domain.Message;
import model.domain.User;

import java.util.Date;

public final class MessageDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final UserDTO sender;
    private final UserDTO receiver;
    private final String content;
    private final Date sentDate;

    public MessageDTO(int id, UserDTO sender, UserDTO receiver, String content, Date sentDate) {
        super();
        if (id != 0) {
            setId(id);
        }
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sentDate = sentDate;
    }

    public MessageDTO(Message message, User sender, User receiver) {
        this(
                message.getId(),
                new UserDTO(sender),
                new UserDTO(receiver),
                message.getContent(),
                message.getSentDate());
    }

    public UserDTO getSender() {
        return sender;
    }

    public UserDTO getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public Date getSentDate() {
        return sentDate;
    }

    @Override
    public String toShortString() {
        return  "ID: " + getId()
                + " | From: " + formatUser(sender)
                + " | To: " + formatUser(receiver)
                + " | Sent: " + formatDate(sentDate)
                + " | Text: " + content;
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nFrom: ").append(formatUser(sender));
        body.append("\nTo: ").append(formatUser(receiver));
        body.append("\nContent: ' ").append(content).append(" '");
        body.append("\nSent: ").append(formatDate(sentDate));
        return section("Message", body.toString());
    }
}
