package model.domain;

import java.util.Date;
import java.util.Objects;
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Comment other = (Comment) obj;
        if (id != 0 || other.id != 0) {
            return id != 0 && id == other.id;
        }
        return Objects.equals(sender, other.sender) && Objects.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(sender, content);
    }

    @Override
    public String toString() {
        return "Comment[id=" + id + ", sender=" + sender + ", content=" + content + ", sentDate=" + sentDate + "]";
    }

}
