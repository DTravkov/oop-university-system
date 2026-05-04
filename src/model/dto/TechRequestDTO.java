package model.dto;

import model.domain.TechRequest;
import model.domain.User;
import model.enumeration.TechRequestStatus;

import java.util.Date;

public final class TechRequestDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final UserDTO sender;
    private final UserDTO receiver;
    private final String content;
    private final TechRequestStatus status;
    private final Date sentDate;

    public TechRequestDTO(int id, UserDTO sender, UserDTO receiver, String content,
                          TechRequestStatus status, Date sentDate) {
        super();
        if (id != 0) {
            setId(id);
        }
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.status = status;
        this.sentDate = sentDate;
    }

    public TechRequestDTO(TechRequest request, User sender, User receiver) {
        this(
                request.getId(),
                new UserDTO(sender),
                new UserDTO(receiver),
                request.getContent(),
                request.getStatus(),
                request.getSentDate());
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

    public TechRequestStatus getStatus() {
        return status;
    }

    public Date getSentDate() {
        return sentDate;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId()
                + " | Status: " + status
                + " | From: " + formatUser(sender)
                + " | To: " + formatUser(receiver)
                + " | Content : " + content;
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nFrom: ").append(formatUser(sender));
        body.append("\nTo: ").append(formatUser(receiver));
        body.append("\nContent: ' ").append(content).append(" '");
        body.append("\nStatus: ").append(status);
        body.append("\nSent: ").append(formatDate(sentDate));
        return section("TechRequest", body.toString());
    }
}
