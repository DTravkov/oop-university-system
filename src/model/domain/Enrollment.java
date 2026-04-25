package model.domain;

import utils.FieldValidator;

import java.util.Date;
import java.util.Objects;

public class Enrollment extends SerializableModel{

	private static final long serialVersionUID = 1L;

	private int courseId;
	private int studentId;
	private Date enrollmentDate;
	private double firstAttestationPoint;
	private double secondAttestationPoint;
	private double finalExamPoint;

	public Enrollment(int courseId, int studentId) {
		this.courseId = courseId;
		this.studentId = studentId;
		this.enrollmentDate = new Date();
		this.firstAttestationPoint = 0.0;
		this.secondAttestationPoint = 0.0;
		this.finalExamPoint = 0.0;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
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

		if (this.getId() != 0 && enr.getId() != 0) {
			return this.getId() == enr.getId();
		}
		return courseId == enr.courseId &&
				studentId == enr.studentId &&
				firstAttestationPoint == enr.firstAttestationPoint &&
				secondAttestationPoint == enr.secondAttestationPoint &&
				finalExamPoint == enr.finalExamPoint;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, courseId, studentId, firstAttestationPoint, secondAttestationPoint, finalExamPoint);
	}

	@Override
	public String toString() {
		return "Enrollment{" +
				"id=" + id +
				", courseId=" + courseId +
				", studentId=" + studentId +
				", firstAttestationPoint=" + firstAttestationPoint +
				", secondAttestationPoint=" + secondAttestationPoint +
				", finalExamPoint=" + finalExamPoint +
				", totalPoint=" + getTotalPoint() +
				'}';
	}
}
