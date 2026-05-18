package model.domain;

import java.util.Date;
import java.util.List;

import utils.StringUtils;
import utils.UIText;


public class Notification extends SerializableModel {

    private final String content;
    private final Date sentDate;
    private User receiver = null;
    List<? extends User> receivers = null;
    private Class<? extends User> multicastClass = null;

    public Notification(UIText content, User receiver) {
        this.content = content.localized();
        this.sentDate = new Date();
        this.receiver = receiver;
    }

    public Notification(UIText content, List<? extends User> receivers) {
        this.content = content.localized();
        this.sentDate = new Date();
        this.receivers = receivers;
    }

    public Notification(UIText content, Class<? extends User> multicastClass) {
        this.content = content.localized();
        this.sentDate = new Date();
        this.multicastClass = multicastClass;
    }

    public String getContent() {
        return content;
    }

    public Date getSentDate() {
        return sentDate;
    }
    
    public User getReceiver() {
        return receiver;
    }

    @SuppressWarnings("unchecked")
    public List<User> getReceivers() {
        return (List<User>) List.copyOf(receivers);
    }

    public Class<? extends User> getMulticastClass() {
        return multicastClass;
    }

    public boolean isUnicast() {
        return receiver != null;
    }

    public boolean isMulticast() {
        return multicastClass != null || receivers != null;
    }


    @Override
    public String asLine() {
        StringBuilder sb = new StringBuilder()
        .append(StringUtils.formatLogTime(sentDate))
        .append(" | ")
        .append(getContent());
        return sb.toString();
    }

    @Override
    public String asTable() {
        return asLine();
    }
}
