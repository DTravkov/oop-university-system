package model.repository;

import java.util.List;
import model.domain.Enrollment;

public class EnrollmentRepository extends Repository<Enrollment> {

    private static final EnrollmentRepository INSTANCE = new EnrollmentRepository();

    private EnrollmentRepository() {
        super();
    }

    public static EnrollmentRepository getInstance() {
        return INSTANCE;
    }


    public Enrollment findByStudentIdAndCourseId(int studentId, int courseId) {
        return this.data.values().stream()
                .filter(entity -> entity.getStudentId() == studentId)
                .filter(entity -> entity.getCourseId() == courseId)
                .findFirst()
                .orElse(null);
    }

    public boolean exists(int studentId, int courseId) {
        return this.findByStudentIdAndCourseId(studentId, courseId) != null;        
    }

    public List<Enrollment> findAllByStudentId(int studentId) {
        return this.data.values().stream()
                .filter(entity -> entity.getStudentId() == studentId)
                .toList();
    }

    public List<Enrollment> findAllByCourseId(int courseId) {
        return this.data.values().stream()
                .filter(entity -> entity.getCourseId() == courseId)
                .toList();
    }

}
