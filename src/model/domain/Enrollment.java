package model.domain;

import utils.FieldValidator;

import java.util.Date;
import java.util.Objects;

public class Enrollment extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private Course course;
    private User student;
    private Date enrollmentDate;
    private double firstAttestationPoint;
    private double secondAttestationPoint;
    private double finalExamPoint;

    public Enrollment(Course course, User student) {
        FieldValidator.requireNonNull(course, "Course");
        FieldValidator.requireNonNull(student, "Student");
        this.course = course;
        this.student = student;
        this.enrollmentDate = new Date();
        this.firstAttestationPoint = 0.0;
        this.secondAttestationPoint = 0.0;
        this.finalExamPoint = 0.0;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        FieldValidator.requireNonNull(course, "Course");
        this.course = course;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
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

    public void setFirstAttestationPoint(double firstAttestationPoint) {
        FieldValidator.requireInRange(firstAttestationPoint, 0, 30, "First attestation point");
        this.firstAttestationPoint = firstAttestationPoint;
    }

    public double getSecondAttestationPoint() {
        return secondAttestationPoint;
    }

    public void setSecondAttestationPoint(double secondAttestationPoint) {
        FieldValidator.requireInRange(secondAttestationPoint, 0, 30, "Second attestation point");
        this.secondAttestationPoint = secondAttestationPoint;
    }

    public double getFinalExamPoint() {
        return finalExamPoint;
    }

    public void setFinalExamPoint(double finalExamPoint) {
        FieldValidator.requireInRange(finalExamPoint, 0, 40, "Final exam point");
        this.finalExamPoint = finalExamPoint;
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
