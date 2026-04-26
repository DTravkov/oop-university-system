package model.domain;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import model.enumeration.NewsUrgencyLevel;
import utils.FieldValidator;

public class News extends SerializableModel {

    private static final long serialVersionUID = 1L;


    private int publisherId;
    private String title;
    private String content;
    private NewsUrgencyLevel urgencyLevel;
    private Date publishedDate;
    private List<Integer> comments;

    public News(int publisherId, String title, String content, NewsUrgencyLevel urgencyLevel) {
        FieldValidator.requirePositive(publisherId, "Publisher ID");
        FieldValidator.requireNonBlank(title, "News title");
        FieldValidator.requireNonBlank(content, "News content");
        FieldValidator.requireNonNull(urgencyLevel, "News urgency level");
        
        this.publisherId = publisherId;
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

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public List<Integer> getComments() {
        return List.copyOf(comments);
    }

    public void addComment(int commentId) {
        this.comments.add(commentId);
    }

    public void removeComment(int commentId) {
        if(this.comments.contains(Integer.valueOf(commentId))){
            this.comments.remove(Integer.valueOf(commentId));
        }
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
                ",publisherId=" + publisherId  +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", urgencyLevel=" + urgencyLevel +
                ", comments=" + comments +
                '}';
    }
}
