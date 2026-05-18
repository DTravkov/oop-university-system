package model.domain;

import java.util.Date;

import exceptions.OperationNotAllowed;
import model.enumeration.ComplaintUrgencyLevel;
import utils.FieldValidator;
import utils.UIText;

public class TeacherComplaint extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private ComplaintUrgencyLevel urgencyLevel;
    private Teacher teacher;
    private Dean dean;
    private Student student;
    private String content;
    private Date sentDate;
    private boolean isClosed = false;

    public TeacherComplaint(ComplaintUrgencyLevel urgencyLevel, Teacher sender, Dean receiver, Student student, String content) {
        FieldValidator.requireNonNull(urgencyLevel);
        FieldValidator.requireNonNull(sender);
        FieldValidator.requireNonNull(receiver);
        FieldValidator.requireNonNull(student);
        FieldValidator.requireNonBlank(content);
        this.urgencyLevel = urgencyLevel;
        this.teacher = sender;
        this.dean = receiver;
        this.student = student;
        this.content = content;
        this.sentDate = new Date();
    }

    public void closeBy(Dean dean){
        if(getDean().getId() != dean.getId()){
            throw new OperationNotAllowed(UIText.ERR_COMPLAINT_CLOSE_NOT_OWNER);
        }
        this.setClosed(true);
        return;
    }

    

    public boolean isClosed() {
        return isClosed;
    }

    private void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public ComplaintUrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(ComplaintUrgencyLevel urgencyLevel) {
        FieldValidator.requireNonNull(urgencyLevel);
        this.urgencyLevel = urgencyLevel;
    }

    public User getTeacher() {
        return teacher;
    }

    public User getDean() {
        return dean;
    }

    public void setDean(Dean dean) {
        FieldValidator.requireNonNull(dean);
        this.dean = dean;
    }

    public User getStudent() {
        return student;
    }

    public String getContent() {
        return content;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        FieldValidator.requireNonNull(sentDate);
        this.sentDate = sentDate;
    }

    @Override
    public String asLine() {
        return String.format("ID: %d | Urgency: %s | Teacher: %s | Dean: %s | About student: %s",
                id, urgencyLevel, teacher.getFullname(), dean.getFullname(), student.getFullname());
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append('\n');
        sb.append("Urgency: ").append(urgencyLevel).append('\n');
        sb.append("Sent: ").append(sentDate).append('\n');
        sb.append("/Teacher/\n").append(teacher.asLine()).append('\n');
        sb.append("/Dean/\n").append(dean.asLine()).append('\n');
        sb.append("/Student/\n").append(student.asLine()).append('\n');
        sb.append("Content:\n").append(content).append('\n');
        return sb.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "[id=" + id +
                ", from=" + teacher +
                ", to=" + dean +
                ", about=" + student +
                ", urgency=" + urgencyLevel +
                ", content=" + content +
                ']';
    }
}
