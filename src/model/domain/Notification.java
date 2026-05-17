package model.domain;

import java.util.Date;

import settings.AppSettings;
import utils.StringUtils;

public class Notification extends SerializableModel {

    private final String content;
    private final Date sentDate;
    private User receiver = null;
    private Class<? extends User> multicastClass = null;

    public Notification(String content, User receiver){
        this.content = content;
        this.sentDate = new Date();
        this.receiver = receiver;
    }

    public Notification(String content, Class<? extends User> multicastClass){
        this.content = content;
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

    public Class<? extends User> getMulticastClass() {
        return multicastClass;
    }

    public boolean isUnicast() {
        return receiver != null;
    }

    public boolean isMulticast() {
        return multicastClass != null;
    }


    @Override
    public String asLine(){
        StringBuilder sb = new StringBuilder()
        .append(StringUtils.formatLogTime(sentDate))
        .append(" | ")
        .append(content);
        return sb.toString();
    }

    //  notifications are not supposed to have a full view
    @Override
    public String asTable() {
        return asLine();
    }


    
}
