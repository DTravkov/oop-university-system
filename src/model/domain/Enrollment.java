package model.domain;

import utils.FieldValidator;

import java.util.Date;
import java.util.Objects;

public class Enrollment extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private Course course;
    private Student student;
    private Date enrollmentDate;

    private double firstAttestationPoint;
    private double secondAttestationPoint;
    private double finalExamPoint;

    private Teacher lectureTeacher;
    private Teacher practiceTeacher;

    public Enrollment(Course course, Student student, Teacher lectureTeacher, Teacher practiceTeacher) {
        FieldValidator.requireNonNull(course, "Course");
        FieldValidator.requireNonNull(student, "Student");
        this.course = course;
        this.student = student;
        this.enrollmentDate = new Date();
        this.firstAttestationPoint = 0.0;
        this.secondAttestationPoint = 0.0;
        this.finalExamPoint = 0.0;
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

    public double getFirstAttestationPoint() {
        return firstAttestationPoint;
    }

    public void incrementFirstAttestationPoint(double delta) {
        double next = firstAttestationPoint + delta;
        FieldValidator.requireInRange(next, 0, 30, "First attestation point");
        this.firstAttestationPoint = next;
    }

    public double getSecondAttestationPoint() {
        return secondAttestationPoint;
    }

    public void incrementSecondAttestationPoint(double delta) {
        double next = secondAttestationPoint + delta;
        FieldValidator.requireInRange(next, 0, 30, "Second attestation point");
        this.secondAttestationPoint = next;
    }

    public double getFinalExamPoint() {
        return finalExamPoint;
    }

    public void incrementFinalExamPoint(double delta) {
        double next = finalExamPoint + delta;
        FieldValidator.requireInRange(next, 0, 40, "Final exam point");
        this.finalExamPoint = next;
    }

    public double getTotalPoint() {
        return firstAttestationPoint + secondAttestationPoint + finalExamPoint;
    }

    public double getGpa() {
        double gpa = (getTotalPoint() / 100.0) * 4.0;
        return Math.round(gpa * 100.0) / 100.0;
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
                && firstAttestationPoint == enr.firstAttestationPoint
                && secondAttestationPoint == enr.secondAttestationPoint
                && finalExamPoint == enr.finalExamPoint;
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(course, student, firstAttestationPoint, secondAttestationPoint, finalExamPoint);
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", course=" + course +
                ", student=" + student +
                ", firstAttestationPoint=" + firstAttestationPoint +
                ", secondAttestationPoint=" + secondAttestationPoint +
                ", finalExamPoint=" + finalExamPoint +
                ", totalPoint=" + getTotalPoint() +
                '}';
    }
}
