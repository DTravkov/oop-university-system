package model.dto;

import model.domain.Comment;
import model.domain.User;

import java.util.Date;

public final class CommentDTO extends BaseViewDTO {
    private static final long serialVersionUID = 1L;

    private final UserDTO author;
    private final Date sentDate;
    private final String content;

    public CommentDTO(Comment comment, User author) {
        super();
        setId(comment.getId());
        this.author = new UserDTO(author);
        this.sentDate = comment.getSentDate();
        this.content = comment.getContent();
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
        String body = "\nID: " + getId()
                + "\nAuthor: " + formatUser(author)
                + "\nDate: " + formatDate(sentDate)
                + "\n ' " + content + " ' ";
        return section("Comment", body);
    }
}
