package model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import exceptions.OperationNotAllowed;
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
        FieldValidator.requireNonNull(publisher);
        FieldValidator.requireNonBlank(title);
        FieldValidator.requireNonBlank(content);
        FieldValidator.requireNonNull(urgencyLevel);

        this.publisher = publisher;
        this.title = title;
        this.content = content;
        this.urgencyLevel = urgencyLevel;
        this.publishedDate = new Date();
        this.comments = new ArrayList<>();
    }


    public void deleteBy(Manager manager){
        if(!getPublisher().equals(manager)){
            throw new OperationNotAllowed("Only the publisher can delete this news item.");
        }
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
        FieldValidator.requireNonNull(publisher);
        this.publisher = publisher;
    }

    public int getPublisherId() {
        return publisher.getId();
    }

    public List<Comment> getComments() {
        return List.copyOf(comments);
    }

    public void addComment(Comment comment) {
        FieldValidator.requireNonNull(comment);
        this.comments.add(comment);
    }

    public boolean removeComment(int commentId) {
        return comments.removeIf(c -> c.getId() == commentId);
    }

    public boolean removeComment(Comment comment) {
        FieldValidator.requireNonNull(comment);
        if (comment.getId() != 0) {
            return removeComment(comment.getId());
        }
        return comments.remove(comment);
    }

    @Override
    public String asLine() {
        return String.format("ID: %d | Title: %s | Urgency: %s | Publisher: %s",
                id, title, urgencyLevel, publisher.getFullname());
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append('\n');
        sb.append("Title: ").append(title).append('\n');
        sb.append("Urgency: ").append(urgencyLevel).append('\n');
        sb.append("Published: ").append(publishedDate).append('\n');
        sb.append("Publisher: ").append(publisher.asLine()).append('\n');
        sb.append("Content:\n").append(content).append('\n');
        sb.append("/Comments/\n");
        for (Comment c : comments) {
            sb.append(c.asLine()).append('\n');
        }
        return sb.toString();
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
