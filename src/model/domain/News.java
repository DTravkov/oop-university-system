package model.domain;

import java.util.Date;
import java.util.Objects;

import model.enumeration.UrgencyLevelEnum;
import utils.FieldValidator;

public class News extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private String title;
    private String content;
    private UrgencyLevelEnum urgencyLevel;
    private Date publishedDate;

    public News(String title, String content, UrgencyLevelEnum urgencyLevel) {
        FieldValidator validator = new FieldValidator();
        validator.requireNonBlank(title, "News title");
        validator.requireNonBlank(content, "News content");
        validator.requireNonNull(urgencyLevel, "Urgency level");
        validator.validate();

        this.title = title;
        this.content = content;
        this.urgencyLevel = urgencyLevel;
        this.publishedDate = new Date();
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

    public UrgencyLevelEnum getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(UrgencyLevelEnum urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
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
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", urgencyLevel=" + urgencyLevel +
                '}';
    }
}
