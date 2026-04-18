package model.domain;

import java.util.Date;
import java.util.Objects;

public class Enrollment extends SerializableModel{

	private static final long serialVersionUID = 1L;

	private Course course;
	private Student student;
	private Date enrollmentDate;

	public Enrollment(Course course, Student student) {
		this.course = course;
		this.student = student;
		this.enrollmentDate = new Date();
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Date getEnrollmentDate() {
		return enrollmentDate;
	}

	public void setEnrollmentDate(Date enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Enrollment enr = (Enrollment) o;

		if (this.getId() != 0 && enr.getId() != 0) {
			return this.getId() == enr.getId();
		}
		return Objects.equals(course, enr.course) &&
				Objects.equals(student, enr.student);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, course, student);
	}

	@Override
	public String toString() {
		return "Enrollment{" +
				"course=" + course +
				", student=" + student +
				", enrollmentDate=" + enrollmentDate +
				'}';
	}
}
