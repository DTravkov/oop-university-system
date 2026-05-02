package utils;

import java.util.Date;

import model.domain.SerializableModel;

public class LogEntry extends SerializableModel {
    private static final long serialVersionUID = 1L;

    public final Date time = new Date();
    public final String action;
    public final int userId;

    public LogEntry(String action, int userId){
        this.action = action;
        this.userId = userId;
    }

    public Date getTime() {
        return time;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        String a = action == null ? "" : action;
        return StringUtils.formatLogTime(time) + " | " + userId + " | " + a + "\n";
    }

    
    
    
}
