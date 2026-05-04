package model.dto;

import model.domain.TeacherComplaint;
import model.domain.User;
import model.enumeration.ComplaintUrgencyLevel;

import java.util.Date;

public final class TeacherComplaintDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final UserDTO sender;
    private final UserDTO receiver;
    private final UserDTO student;
    private final ComplaintUrgencyLevel urgencyLevel;
    private final String content;
    private final Date sentDate;

    public TeacherComplaintDTO(int id, UserDTO sender, UserDTO receiver, UserDTO student,
                               ComplaintUrgencyLevel urgencyLevel, String content, Date sentDate) {
        super();
        if (id != 0) {
            setId(id);
        }
        this.sender = sender;
        this.receiver = receiver;
        this.student = student;
        this.urgencyLevel = urgencyLevel;
        this.content = content;
        this.sentDate = sentDate;
    }

    public TeacherComplaintDTO(TeacherComplaint complaint, User sender, User receiver, User student) {
        this(
                complaint.getId(),
                new UserDTO(sender),
                new UserDTO(receiver),
                new UserDTO(student),
                complaint.getUrgencyLevel(),
                complaint.getContent(),
                complaint.getSentDate());
    }

    public UserDTO getSender() {
        return sender;
    }

    public UserDTO getReceiver() {
        return receiver;
    }

    public UserDTO getStudent() {
        return student;
    }

    public ComplaintUrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public String getContent() {
        return content;
    }

    public Date getSentDate() {
        return sentDate;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId()
                + " | Urgency: " + urgencyLevel
                + " | From: " + formatUser(sender)
                + " | To: " + formatUser(receiver)
                + " | Student: " + formatUser(student)
                + " | Text: " + content;
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nFrom: ").append(formatUser(sender));
        body.append("\nTo: ").append(formatUser(receiver));
        body.append("\nStudent: ").append(formatUser(student));
        body.append("\nUrgency: ").append(urgencyLevel);
        body.append("\nContent: ' ").append(content).append(" '");
        body.append("\nSent: ").append(formatDate(sentDate));
        return section("TeacherComplaint", body.toString());
    }
}
