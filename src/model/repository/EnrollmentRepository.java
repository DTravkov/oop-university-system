package model.repository;


import model.domain.Enrollment;

public class EnrollmentRepository extends MapRepository<Enrollment> {
	
			
	@Override
	protected String getFilePath() {
		return "enrollments.ser";
	}
	

}
