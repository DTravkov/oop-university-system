package model.dto;

import model.domain.News;
import model.domain.User;
import model.enumeration.NewsUrgencyLevel;

import java.util.Date;
import java.util.List;

public final class NewsDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final String title;
    private final UserDTO publisher;
    private final NewsUrgencyLevel urgencyLevel;
    private final Date publishedDate;
    private final String content;
    private final List<CommentDTO> comments;

    public NewsDTO(int id, String title, UserDTO publisher, NewsUrgencyLevel urgencyLevel,
                   Date publishedDate, String content, List<CommentDTO> comments) {
        super();
        if (id != 0) {
            setId(id);
        }
        this.title = title;
        this.publisher = publisher;
        this.urgencyLevel = urgencyLevel;
        this.publishedDate = publishedDate;
        this.content = content;
        this.comments = comments == null ? List.of() : List.copyOf(comments);
    }

    public NewsDTO(News news, User publisher, List<CommentDTO> comments) {
        this(
                news.getId(),
                news.getTitle(),
                new UserDTO(publisher),
                news.getUrgencyLevel(),
                news.getPublishedDate(),
                news.getContent(),
                comments);
    }

    public String getTitle() {
        return title;
    }

    public UserDTO getPublisher() {
        return publisher;
    }

    public NewsUrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public String getContent() {
        return content;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId()
                + " | Title: " + title
                + " | Publisher: " + formatUser(publisher)
                + " | Urgency: " + urgencyLevel;
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nTitle: ").append(title);
        body.append("\nPublisher: ").append(formatUser(publisher));
        body.append("\nUrgency: ").append(urgencyLevel);
        body.append("\nPublished: ").append(formatDate(publishedDate));
        body.append("\nContent: ' ").append(content).append(" '");
        body.append("\nComments:");
        for (CommentDTO c : comments) {
            body.append(c.toString());
        }
        return section("News", body.toString());
    }
}
