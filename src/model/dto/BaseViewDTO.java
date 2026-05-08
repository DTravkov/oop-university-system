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

    protected static String section(String dtoName, String body) {
        return "\n/// " + dtoName + " ///" + body;
    }

    protected static String formatDate(Date date) {
        return StringUtils.formatLogTime(date);
    }

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

    protected static String formatResearchProjectList(List<ResearchProjectDTO> projects) {
        if (projects == null || projects.isEmpty()) {
            return "_";
        }
        StringBuilder sb = new StringBuilder();
        for (ResearchProjectDTO p : projects) {
            sb.append(p.toShortString() + "\n");
        }
        return sb.toString();
    }
    protected static String formatResearchPaperList(List<ResearchPaperDTO> papers) {
        if (papers == null || papers.isEmpty()) {
            return "_";
        }
        StringBuilder sb = new StringBuilder();
        for (ResearchPaperDTO p : papers) {
            sb.append(p.toShortString() + "\n");
        }
        return sb.toString();
    }

}
