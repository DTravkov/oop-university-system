package model.dto;

import model.domain.Message;

import java.util.Date;

public final class MessageDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final UserDTO sender;
    private final UserDTO receiver;
    private final String content;
    private final Date sentDate;

    public MessageDTO(Message message, UserDTO sender, UserDTO receiver) {
        super();
        setId(message.getId());
        this.sender = sender;
        this.receiver = receiver;
        this.content = message.getContent();
        this.sentDate = message.getSentDate();
    }

    public UserDTO getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public UserDTO getReceiver() {
        return receiver;
    }

    public UserDTO getOtherUser(int mainUserId) {
        if(this.getSender().getId() != mainUserId){
            return this.getSender();
        }
        if(this.getReceiver().getId() != mainUserId){
            return this.getReceiver();
        }
        return null;
    }


    public Date getSentDate() {
        return sentDate;
    }

    @Override
    public String toShortString() {
        return 
                " -|" +sender.getName() + " " + sender.getSurname() + " (" + formatDate(sentDate) + "): "
                + "\n" + content;
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
