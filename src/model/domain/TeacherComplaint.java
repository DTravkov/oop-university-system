package model.domain;

import java.util.Date;
import java.util.Objects;

import model.enumeration.ComplaintUrgencyLevel;
import utils.FieldValidator;

public class TeacherComplaint extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private ComplaintUrgencyLevel urgencyLevel;
    private User sender;
    private User receiver;
    private User student;
    private String content;
    private Date sentDate;

    public TeacherComplaint(ComplaintUrgencyLevel urgencyLevel, User sender, User receiver, User student, String content) {
        FieldValidator.requireNonNull(urgencyLevel, "Urgency level");
        FieldValidator.requireNonNull(sender, "Sender");
        FieldValidator.requireNonNull(receiver, "Receiver");
        FieldValidator.requireNonNull(student, "Student");
        FieldValidator.requireNonBlank(content, "Content");
        this.urgencyLevel = urgencyLevel;
        this.sender = sender;
        this.receiver = receiver;
        this.student = student;
        this.content = content;
        this.sentDate = new Date();
    }

    public ComplaintUrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(ComplaintUrgencyLevel urgencyLevel) {
        FieldValidator.requireNonNull(urgencyLevel, "Urgency level");
        this.urgencyLevel = urgencyLevel;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        FieldValidator.requireNonNull(sender, "Sender");
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        FieldValidator.requireNonNull(receiver, "Receiver");
        this.receiver = receiver;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        FieldValidator.requireNonNull(student, "Student");
        this.student = student;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        FieldValidator.requireNonBlank(content, "Content");
        this.content = content;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        FieldValidator.requireNonNull(sentDate, "Sent date");
        this.sentDate = sentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeacherComplaint that = (TeacherComplaint) o;
        if (id != 0 && that.getId() != 0) {
            return id == that.getId();
        }
        return urgencyLevel == that.urgencyLevel
                && Objects.equals(sender, that.sender)
                && Objects.equals(receiver, that.receiver)
                && Objects.equals(student, that.student)
                && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(urgencyLevel, sender, receiver, student, content);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "[id=" + id +
                ", from=" + sender +
                ", to=" + receiver +
                ", aboutStudent=" + student +
                ", urgency=" + urgencyLevel +
                ", content=" + content +
                ']';
    }
}
