package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Enrollment implements Serializable, Indexed {
	private int id;
	private int studentId;
	private int lecturerId;
	private int practiceId;
	private int courseId;
	private Date enrollmentDate;
	
	

	public Enrollment(int studentId, int lecturerId, int practiceId, int courseId) {
		this.studentId = studentId;
		this.lecturerId = lecturerId;
		this.practiceId = practiceId;
		this.courseId = courseId;
		this.enrollmentDate = new Date();
	}
	
	
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}


	public int getStudentId() {
		return studentId;
	}


	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}


	public int getLecturerId() {
		return lecturerId;
	}


	public void setLecturerId(int lecturerId) {
		this.lecturerId = lecturerId;
	}


	public int getPracticeId() {
		return practiceId;
	}


	public void setPracticeId(int practiceId) {
		this.practiceId = practiceId;
	}


	public int getCourseId() {
		return courseId;
	}


	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}


	public Date getEnrollmentDate() {
		return enrollmentDate;
	}


	public void setEnrollmentDate(Date enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}



	@Override
	public int hashCode() {
		return Objects.hash(courseId, enrollmentDate, id, lecturerId, practiceId, studentId);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Enrollment other = (Enrollment) obj;
		return courseId == other.courseId && Objects.equals(enrollmentDate, other.enrollmentDate) && id == other.id
				&& lecturerId == other.lecturerId && practiceId == other.practiceId && studentId == other.studentId;
	}
	
	
	
	
	
	
	
	
}
