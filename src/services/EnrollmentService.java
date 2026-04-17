package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.Enrollment;
import model.EnrollmentRepository;

public class EnrollmentService {
	private static final EnrollmentRepository repo = new EnrollmentRepository();
	
	
	
	
	public void enroll(int studentId, int lecturerId, int practiceId, int courseId) {
		this.checkNotExist(studentId, courseId);
		Enrollment enr = new Enrollment(studentId,lecturerId, practiceId, courseId);
		this.repo.add(studentId, enr);
	}
	
	public void drop(int studentId, int courseId) {
		Enrollment enr = this.getEnrollment(studentId, courseId);
		this.repo.delete(studentId, enr);
	}
	
	public Enrollment checkNotExist(int studentId, int courseId) {
		Enrollment enr = this.getEnrollment(studentId, courseId);
		if(enr != null) throw new AlreadyExists("This enrollment already exists");
		return enr;
	}
	
	public Enrollment getEnrollment(int studentId, int courseId) {
		for(Enrollment enr : this.repo.getAllByOwnerId(studentId)) {
			if(enr.getCourseId() == courseId) return enr;
		}
		throw new DoesNotExist("This enrollment does not exist");
	}
	public Enrollment getEnrollment(int enrollmentId) {
		Enrollment enr = this.repo.getValueById(enrollmentId);
		if(enr == null) throw new DoesNotExist("This enrollment does not exist");
		return enr;
	}
	
	
	
	
}
