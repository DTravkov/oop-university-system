package model.domain;

import model.enumeration.ComplaintUrgencyLevel;

public class TeacherComplaint extends Message {

    private final ComplaintUrgencyLevel urgencyLevel;
    private final int deanId;
    private final int studentId;

    public TeacherComplaint(ComplaintUrgencyLevel urgrencyLevel, int teacherId, int deanId, int studentId, String content) {
        super(teacherId, deanId, content);
        this.urgencyLevel = urgrencyLevel;
        this.deanId = deanId;
        this.studentId = studentId;
    }

    public ComplaintUrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public int getStudentId() {
        return studentId;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + 
        "[id=" + id + 
        ", from=" + getSenderId() + 
        ", to=" + getReceiverId() + 
        ", student=" + getStudentId() + 
        ", content=" + getContent() + 
        "]";
    }
    
    
        
}
