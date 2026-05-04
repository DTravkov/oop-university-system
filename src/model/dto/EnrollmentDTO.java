package model.dto;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.User;

import java.util.Date;

public final class EnrollmentDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final String courseName;
    private final int courseId;
    private final UserDTO student;
    private final Date enrollmentDate;
    private final double firstAttestationPoint;
    private final double secondAttestationPoint;
    private final double finalExamPoint;
    private final double totalPoint;
    private final double gpa;

    public EnrollmentDTO(int id, String courseName, int courseId, UserDTO student,
                         Date enrollmentDate, double firstAttestationPoint, double secondAttestationPoint,
                         double finalExamPoint, double totalPoint, double gpa) {
        super();
        if (id != 0) {
            setId(id);
        }
        this.courseName = courseName;
        this.courseId = courseId;
        this.student = student;
        this.enrollmentDate = enrollmentDate;
        this.firstAttestationPoint = firstAttestationPoint;
        this.secondAttestationPoint = secondAttestationPoint;
        this.finalExamPoint = finalExamPoint;
        this.totalPoint = totalPoint;
        this.gpa = gpa;
    }

    public EnrollmentDTO(Enrollment enrollment, Course course, User student) {
        this(
                enrollment.getId(),
                course.getName(),
                course.getId(),
                new UserDTO(student),
                enrollment.getEnrollmentDate(),
                enrollment.getFirstAttestationPoint(),
                enrollment.getSecondAttestationPoint(),
                enrollment.getFinalExamPoint(),
                enrollment.getTotalPoint(),
                enrollment.getGpa());
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCourseId() {
        return courseId;
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

    @Override
    public String toShortString() {
        return "ID: " + getId()
                + " | Course: " + courseName + " (id=" + courseId + ")"
                + " | Student: " + formatUser(student)
                + " | GPA: " + gpa;
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nCourse: ").append(courseName).append(" (id=").append(courseId).append(")");
        body.append("\nStudent: ").append(formatUser(student));
        body.append("\nEnrolled: ").append(formatDate(enrollmentDate));
        body.append("\nFirst attestation: ").append(firstAttestationPoint);
        body.append("\nSecond attestation: ").append(secondAttestationPoint);
        body.append("\nFinal exam: ").append(finalExamPoint);
        body.append("\nTotal: ").append(totalPoint);
        body.append("\nGPA: ").append(gpa);
        return section("Enrollment", body.toString());
    }
}
