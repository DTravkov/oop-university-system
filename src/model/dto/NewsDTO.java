package model.dto;

import model.domain.News;
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

    public NewsDTO(News news, UserDTO publisher, List<CommentDTO> comments) {
        super();
        setId(news.getId());
        this.title = news.getTitle();
        this.publisher = publisher;
        this.urgencyLevel = news.getUrgencyLevel();
        this.publishedDate = news.getPublishedDate();
        this.content = news.getContent();
        this.comments = comments == null ? List.of() : List.copyOf(comments);
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
            body.append( "\n   " + c.getAuthor().getName() + " " +c.getAuthor().getSurname()  + "(" + c.getAuthor().getId() + ")"+ " : " + c.getContent());
        }
        return section("News", body.toString());
    }
}
