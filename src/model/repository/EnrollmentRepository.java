package model.repository;

import model.domain.Enrollment;
import model.domain.Student;

public class EnrollmentRepository extends Repository<Enrollment> {

    public EnrollmentRepository() {
        super("enrollments.ser");
    }

    public Enrollment getStudentEnrollments(Student student) {
        return this.getStudentEnrollments(student.getId());
    }

    public Enrollment getStudentEnrollments(int studentId) {
        return this.data.values().stream()
                .filter(entity -> entity.getStudent().getId() == studentId)
                .findFirst()
                .orElse(null);
    }

}
