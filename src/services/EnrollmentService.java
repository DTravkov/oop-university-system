package services;

import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.User;
import model.repository.EnrollmentRepository;

public class EnrollmentService extends BaseService<Enrollment, EnrollmentRepository> {

    private final UserService userService;
    private final CourseService courseService;

    public EnrollmentService() {
        super(new EnrollmentRepository());
        this.userService = new UserService();
        this.courseService = new CourseService();
    }

    public void createEnrollment(Enrollment enrollment) {
        User student = userService.get(enrollment.getStudent().getId());
        if (!(student instanceof Student validStudent)) {
            throw new OperationNotAllowed("Cannot enroll a non-student user");
        }

        Course course = courseService.get(enrollment.getCourse().getId());
        enrollment.setStudent(validStudent);
        enrollment.setCourse(course);

        repository.save(enrollment);
    }

    public void deleteEnrollment(Student student, Course course) {
        Enrollment enrollment = getByStudentAndCourse(student, course);
        repository.delete(enrollment);
    }

    public Enrollment getByStudentAndCourse(Student student, Course course) {
        return getAll().stream()
                .filter(enrollment -> enrollment.getStudent().getId() == student.getId())
                .filter(enrollment -> enrollment.getCourse().getId() == course.getId())
                .findFirst()
                .orElseThrow(() -> new DoesNotExist("Enrollment for student id " + student.getId() + " and course id " + course.getId()));
    }
}
