package model;


public class EnrollmentRepository extends MapRepository<Enrollment> {
	
			
	@Override
	protected String getFilePath() {
		return "enrollments.ser";
	}
	

}
