package model.dto;

import model.domain.TechRequest;
import model.enumeration.TechRequestStatus;

import java.util.Date;

public final class TechRequestDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final UserDTO sender;
    private final UserDTO receiver;
    private final String content;
    private final TechRequestStatus status;
    private final Date sentDate;

    public TechRequestDTO(TechRequest request, UserDTO sender, UserDTO receiver) {
        super();
        setId(request.getId());
        this.sender = sender;
        this.receiver = receiver;
        this.content = request.getContent();
        this.status = request.getStatus();
        this.sentDate = request.getSentDate();
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
