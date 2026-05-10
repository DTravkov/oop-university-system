package utils;

import java.util.Date;

import model.domain.SerializableModel;
import model.domain.User;

public class LogEntry extends SerializableModel {
    private static final long serialVersionUID = 1L;

    public final Date time = new Date();
    public final String action;
    public final int userId;
    public final String fullname;

    public LogEntry(String action, User user){
        this.action = action;
        this.userId = user.getId();
        this.fullname = user.getFullname();
    }

    public Date getTime() {
        return time;
    }

    public String getAction() {
        return action;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullname() {
        return fullname;
    }

    @Override
    public String toString() {
        String a = action == null ? "" : action;
        return StringUtils.formatLogTime(time) + " | " + fullname +", id=" + userId + " | " + a;
    }

    
    
    
}
