package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.Enrollment;
import model.EnrollmentRepository;

public class EnrollmentService {
	
	protected EnrollmentRepository repository;
	
	public EnrollmentService() {
		this(new EnrollmentRepository());
	}
	public EnrollmentService(EnrollmentRepository repository) {
		this.repository = repository;
	}
	
	
	public void enroll(int studentId, int lecturerId, int practiceId, int courseId) {
		this.checkNotExist(studentId, courseId);
		Enrollment enr = new Enrollment(studentId,lecturerId, practiceId, courseId);
		this.repository.add(studentId, enr);
	}
	
	public void drop(int studentId, int courseId) {
		Enrollment enr = this.findOrThrow(studentId, courseId);
		this.repository.delete(studentId, enr);
	}

	
	public Enrollment findOrThrow(int studentId, int courseId) {
		for (Enrollment enr : this.repository.getAllByOwnerId(studentId)) {
			if (enr.getCourseId() == courseId) {
				return enr;
			}
		}
		throw new DoesNotExist("This enrollment does not exist");
	}
	public Enrollment findOrThrow(int enrollmentId) {
		Enrollment enr = this.repository.getValueById(enrollmentId);
		if (enr == null) {
			throw new DoesNotExist("This enrollment does not exist");
		}
		return enr;
	}
	
	public Enrollment checkNotExist(int studentId, int courseId) {
		Enrollment enr = this.findOrThrow(studentId, courseId);
		if(enr != null) throw new AlreadyExists("This enrollment already exists");
		return enr;
	}
	
	
	
}
