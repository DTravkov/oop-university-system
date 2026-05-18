package model.domain;

import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.enumeration.AttestationType;
import utils.FieldValidator;
import utils.UIText;

import java.util.Date;

public class Enrollment extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private final Course course;
    private final Student student;
    private final Mark mark;

    private Date enrollmentDate;

    private Teacher lectureTeacher;
    private Teacher practiceTeacher;

    public Enrollment(Course course, Student student, Teacher lectureTeacher, Teacher practiceTeacher) {
        FieldValidator.requireNonNull(course);
        FieldValidator.requireNonNull(student);
        this.course = course;
        if (course.getLectureTeachers().isEmpty() || course.getPracticeTeachers().isEmpty()) {
            throw new DoesNotExist(UIText.ERR_ENROLLMENT_NO_TEACHERS);
        }
        this.student = student;
        this.enrollmentDate = new Date();
        this.mark = new Mark();
        this.lectureTeacher = lectureTeacher;
        this.practiceTeacher = practiceTeacher;
    }

    public Teacher getLectureTeacher() {
        return lectureTeacher;
    }

    public void setLectureTeacher(Teacher lectureTeacher) {
        this.lectureTeacher = lectureTeacher;
    }

    public Teacher getPracticeTeacher() {
        return practiceTeacher;
    }

    public void setPracticeTeacher(Teacher practiceTeacher) {
        this.practiceTeacher = practiceTeacher;
    }

    public boolean isTeaching(Teacher teacher) {
        return this.lectureTeacher.equals(teacher)
                || this.practiceTeacher.equals(teacher);
    }

    public Course getCourse() {
        return course;
    }

    public Student getStudent() {
        return student;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Mark getMark() {
        return mark;
    }

    public double getFirstAttestationPoint() {
        return mark.getFirstAttestationPoint();
    }

    public double getSecondAttestationPoint() {
        return mark.getSecondAttestationPoint();
    }

    public double getFinalExamPoint() {
        return mark.getFinalExamPoint();
    }

    public void incrementMark(double point, Teacher teacher, AttestationType attestationType) {
        if (!isTeaching(teacher)) {
            throw new OperationNotAllowed(UIText.ERR_ENROLLMENT_GRADE_NOT_TEACHER);
        }
        mark.addPoints(attestationType, point);
    }

    public double getTotalPoint() {
        return mark.getTotalPoint();
    }

    public double getGpa() {
        return mark.getGpa();
    }

    @Override
    public String asLine() {
        return String.format("ID: %d | Course: %s | Student: %s | Total: %.1f | GPA: %.2f",
                id, course.getName(), student.getFullname(), getTotalPoint(), getGpa());
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append('\n');
        sb.append("Course: ").append(course.getName()).append('\n');
        sb.append("Lecture teacher: ").append(lectureTeacher.getFullname()).append('\n');
        sb.append("Practice teacher: ").append(practiceTeacher.getFullname()).append('\n');
        sb.append("/Marks/\n").append(mark.asTable());
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", course=" + course +
                ", student=" + student +
                ", mark=" + mark +
                ", totalPoint=" + getTotalPoint() +
                '}';
    }
}
