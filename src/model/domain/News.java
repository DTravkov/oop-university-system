package model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import model.enumeration.NewsUrgencyLevel;
import utils.FieldValidator;

public class News extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private User publisher;
    private String title;
    private String content;
    private NewsUrgencyLevel urgencyLevel;
    private Date publishedDate;
    private List<Comment> comments;

    public News(User publisher, String title, String content, NewsUrgencyLevel urgencyLevel) {
        FieldValidator.requireNonNull(publisher, "Publisher");
        FieldValidator.requireNonBlank(title, "News title");
        FieldValidator.requireNonBlank(content, "News content");
        FieldValidator.requireNonNull(urgencyLevel, "News urgency level");

        this.publisher = publisher;
        this.title = title;
        this.content = content;
        this.urgencyLevel = urgencyLevel;
        this.publishedDate = new Date();
        this.comments = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NewsUrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(NewsUrgencyLevel urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        FieldValidator.requireNonNull(publisher, "Publisher");
        this.publisher = publisher;
    }

    public int getPublisherId() {
        return publisher.getId();
    }

    public List<Comment> getComments() {
        return List.copyOf(comments);
    }

    public void addComment(Comment comment) {
        FieldValidator.requireNonNull(comment, "Comment");
        this.comments.add(comment);
    }

    public boolean removeComment(int commentId) {
        return comments.removeIf(c -> c.getId() == commentId);
    }

    public boolean removeComment(Comment comment) {
        FieldValidator.requireNonNull(comment, "Comment");
        if (comment.getId() != 0) {
            return removeComment(comment.getId());
        }
        return comments.remove(comment);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        if (this.id != 0 && news.getId() != 0) return this.id == news.getId();
        return Objects.equals(title, news.title);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", publisher=" + publisher +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", urgencyLevel=" + urgencyLevel +
                ", comments=" + comments +
                '}';
    }

}
