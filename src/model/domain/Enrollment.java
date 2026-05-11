package model.domain;

import exceptions.OperationNotAllowed;
import model.enumeration.AttestationType;
import utils.FieldValidator;

import java.util.Date;
import java.util.Objects;

public class Enrollment extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private Course course;
    private Student student;
    private Date enrollmentDate;

    private Teacher lectureTeacher;
    private Teacher practiceTeacher;

    private final Mark mark;

    public Enrollment(Course course, Student student, Teacher lectureTeacher, Teacher practiceTeacher) {
        FieldValidator.requireNonNull(course, "Course");
        FieldValidator.requireNonNull(student, "Student");
        this.course = course;
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
        return this.lectureTeacher.getId() == teacher.getId()
                || this.practiceTeacher.getId() == teacher.getId();
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        FieldValidator.requireNonNull(course, "Course");
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        FieldValidator.requireNonNull(student, "Student");
        this.student = student;
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
            throw new OperationNotAllowed("putting marks for other teacher students");
        }
        mark.increment(attestationType, point);
    }

    public double getTotalPoint() {
        return mark.getTotalPoint();
    }

    public double getGpa() {
        return mark.getGpa();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment enr = (Enrollment) o;

        if (id != 0 || enr.getId() != 0) {
            return id != 0 && id == enr.getId();
        }
        return Objects.equals(course, enr.course)
                && Objects.equals(student, enr.student)
                && Objects.equals(mark, enr.mark);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(course, student, mark);
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
