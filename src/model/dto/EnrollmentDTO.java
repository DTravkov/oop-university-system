package model.dto;

import model.domain.Course;
import model.domain.Enrollment;

import java.util.Date;

public final class EnrollmentDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final CourseDTO course;
    private final UserDTO student;
    private final Date enrollmentDate;
    private final double firstAttestationPoint;
    private final double secondAttestationPoint;
    private final double finalExamPoint;
    private final double totalPoint;
    private final double gpa;
    private final UserDTO lectureTeacher;
    private final UserDTO practiceTeacher;

    public EnrollmentDTO(Enrollment enrollment, CourseDTO course, UserDTO student, UserDTO lectureTeacher, UserDTO practiceTeacher) {
        super();
        setId(enrollment.getId());
        this.course = course;

        this.student = student;
        this.enrollmentDate = enrollment.getEnrollmentDate();
        this.firstAttestationPoint = enrollment.getFirstAttestationPoint();
        this.secondAttestationPoint = enrollment.getSecondAttestationPoint();
        this.finalExamPoint = enrollment.getFinalExamPoint();
        this.totalPoint = enrollment.getTotalPoint();
        this.gpa = enrollment.getGpa();
        this.lectureTeacher = lectureTeacher;
        this.practiceTeacher = practiceTeacher;
    }

    public UserDTO getStudent() {
        return student;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public double getFirstAttestationPoint() {
        return firstAttestationPoint;
    }

    public double getSecondAttestationPoint() {
        return secondAttestationPoint;
    }

    public double getFinalExamPoint() {
        return finalExamPoint;
    }

    public double getTotalPoint() {
        return totalPoint;
    }

    public double getGpa() {
        return gpa;
    }

    public UserDTO getLectureTeacher() {
        return lectureTeacher;
    }

    public UserDTO getPracticeTeacher() {
        return practiceTeacher;
    }

    public CourseDTO getCourse() {
        return course;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId()
                + " | Course: " + course.toShortString()
                + " | Student: " + formatUser(student)
                + " | Lecture Teacher: " + formatUser(lectureTeacher)
                + " | Practice Teacher: " + formatUser(practiceTeacher)
                + " | GPA: " + gpa;
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nCourse: ").append(course.toShortString());
        body.append("\nStudent: ").append(formatUser(student));
        body.append("\nLecture Teacher: ").append(formatUser(lectureTeacher));
        body.append("\nPractice Teacher: ").append(formatUser(practiceTeacher));
        body.append("\nEnrolled: ").append(formatDate(enrollmentDate));
        body.append("\nFirst attestation: ").append(firstAttestationPoint);
        body.append("\nSecond attestation: ").append(secondAttestationPoint);
        body.append("\nFinal exam: ").append(finalExamPoint);
        body.append("\nTotal: ").append(totalPoint);
        body.append("\nGPA: ").append(gpa);
        return section("Enrollment", body.toString());
    }
}
