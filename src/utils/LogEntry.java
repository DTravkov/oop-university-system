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
    public String asLine() {
        String a = action == null ? "" : action;
        return String.format("Log | ID: %d | %s | %s", userId, fullname, a);
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Time: ").append(StringUtils.formatLogTime(time)).append('\n');
        sb.append("User id: ").append(userId).append('\n');
        sb.append("Full name: ").append(fullname).append('\n');
        sb.append("Action:\n").append(action == null ? "" : action).append('\n');
        return sb.toString();
    }

    @Override
    public String toString() {
        String a = action == null ? "" : action;
        return StringUtils.formatLogTime(time) + " | " + fullname +", id=" + userId + " | " + a;
    }

    
    
    
}
