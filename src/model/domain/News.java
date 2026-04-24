package model.domain;

import java.util.Date;
import java.util.Objects;

import model.enumeration.NewsUrgencyLevel;
import utils.FieldValidator;

public class News extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private String title;
    private String content;
    private NewsUrgencyLevel urgencyLevel;
    private Date publishedDate;

    public News(String title, String content, NewsUrgencyLevel urgencyLevel) {
        FieldValidator.requireNonBlank(title, "News title");
        FieldValidator.requireNonBlank(content, "News content");
        FieldValidator.requireNonNull(urgencyLevel, "Urgency level");

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
