package model.domain;

import java.util.Date;
import utils.FieldValidator;

public class Comment extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private final User sender;
    private final String content;
    private final Date sentDate;

    public Comment(User sender, String content) {
        FieldValidator.requireNonNull(sender, "Sender");
        FieldValidator.requireNonBlank(content, "Content");
        this.sender = sender;
        this.content = content;
        this.sentDate = new Date();
    }

    public User getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Date getSentDate() {
        return sentDate;
    }

    @Override
    public String asLine() {
        String preview = content.length() > 36 ? content.substring(0, 33) + "..." : content;
        return String.format(" %s | %s", sender.getFullname(), preview);
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sender:\n").append(sender.asLine()).append('\n');
        sb.append("Sent: ").append(sentDate).append('\n');
        sb.append("Content:\n").append(content).append('\n');
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Comment[id=" + id + ", sender=" + sender + ", content=" + content + ", sentDate=" + sentDate + "]";
    }

}
