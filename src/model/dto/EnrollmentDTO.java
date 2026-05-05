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
    private final UserDTO lectureTeacher;
    private final UserDTO practiceTeacher;

    public EnrollmentDTO(Enrollment enrollment, Course course, User student, User lectureTeacher, User practiceTeacher) {
        super();
        setId(enrollment.getId());
        this.courseName = course.getName();
        this.courseId = course.getId();
        this.student = new UserDTO(student);
        this.enrollmentDate = enrollment.getEnrollmentDate();
        this.firstAttestationPoint = enrollment.getFirstAttestationPoint();
        this.secondAttestationPoint = enrollment.getSecondAttestationPoint();
        this.finalExamPoint = enrollment.getFinalExamPoint();
        this.totalPoint = enrollment.getTotalPoint();
        this.gpa = enrollment.getGpa();
        this.lectureTeacher = new UserDTO(lectureTeacher);
        this.practiceTeacher = new UserDTO(practiceTeacher);
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

    public UserDTO getLectureTeacher() {
        return lectureTeacher;
    }

    public UserDTO getPracticeTeacher() {
        return practiceTeacher;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId()
                + " | Course: " + courseName + " (id=" + courseId + ")"
                + " | Student: " + formatUser(student)
                + " | Lecture Teacher: " + formatUser(lectureTeacher)
                + " | Practice Teacher: " + formatUser(practiceTeacher)
                + " | GPA: " + gpa;
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nCourse: ").append(courseName).append(" (id=").append(courseId).append(")");
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
