package model.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import utils.StringUtils;

public abstract class BaseViewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    protected BaseViewDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract String toShortString();

    protected static String section(String typeName, String body) {
        String b = body.startsWith("\n") ? body.substring(1) : body;
        return "/// " + typeName + "\n" + b;
    }

    protected static String formatDate(Date date) {
        return StringUtils.formatLogTime(date);
    }

    /** Wraps a user's one-line summary in parentheses for readability next to labels. */
    protected static String formatUser(UserDTO user) {
        return "(" + user.toShortString() + ")";
    }

    protected static String formatUserList(List<UserDTO> users) {
        if (users == null || users.isEmpty()) {
            return "_";
        }
        StringBuilder sb = new StringBuilder();
        for (UserDTO u : users) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(formatUser(u));
        }
        return sb.toString();
    }

}
