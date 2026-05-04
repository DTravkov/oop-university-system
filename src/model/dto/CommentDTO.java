package model.dto;

import model.domain.Comment;
import model.domain.User;

import java.util.Date;

public final class CommentDTO extends BaseViewDTO {
    private static final long serialVersionUID = 1L;

    private final UserDTO author;
    private final Date sentDate;
    private final String content;

    public CommentDTO(int id, UserDTO author, Date sentDate, String content) {
        super();
        if (id != 0) {
            setId(id);
        }
        this.author = author;
        this.sentDate = sentDate;
        this.content = content;
    }

    public CommentDTO(Comment comment, User author) {
        this(comment.getId(), new UserDTO(author), comment.getSentDate(), comment.getContent());
    }

    public UserDTO getAuthor() {
        return author;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId()
                + " | Author: " + formatUser(author)
                + " | Date: " + formatDate(sentDate)
                + " | Text: " + content;
    }

    @Override
    public String toString() {
        return "\nID: " + getId()
                + "\nAuthor: " + formatUser(author)
                + "\nDate: " + formatDate(sentDate)
                + "\n ' " + content + " ' ";
    }
}
