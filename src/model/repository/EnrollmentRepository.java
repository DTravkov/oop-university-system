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
        return this.findFirst(entity -> entity.getStudentId() == studentId && entity.getCourseId() == courseId)
                .orElse(null);
    }

    public List<Enrollment> findAllByStudentId(int studentId) {
        return this.findAll(entity -> entity.getStudentId() == studentId);
    }

    public List<Enrollment> findAllByCourseId(int courseId) {
        return this.findAll(entity -> entity.getCourseId() == courseId);
    }

    public boolean exists(int studentId, int courseId) {
        return this.exists(entity -> entity.getStudentId() == studentId && entity.getCourseId() == courseId);
    }

}
