package services;

import java.util.Collection;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.User;
import model.repository.EnrollmentRepository;

public class EnrollmentService {

    protected final EnrollmentRepository repository;
    private final UserService userService;
    private CourseService courseService;

    public EnrollmentService() {
        this(new EnrollmentRepository(), new UserService(), new CourseService());
    }

    public EnrollmentService(EnrollmentRepository repository, UserService userService, CourseService courseService) {
        this.repository = repository;
        this.userService = userService;
        this.courseService = courseService;
    }

    public void createEnrollment(Enrollment enr) {
        if (enr.getStudent() == null) {
            throw new OperationNotAllowed("create enrollment without student");
        }
        if (enr.getCourse() == null) {
            throw new OperationNotAllowed("create enrollment without course");
        }

        User student = userService.findOrThrow(enr.getStudent().getId());
        if (!(student instanceof Student validStudent)) {
            throw new OperationNotAllowed("create enrollment for a User who is not a student");
        }

        Course course = courseService.findOrThrow(enr.getCourse().getId());
        enr.setStudent(validStudent);
        enr.setCourse(course);

        ensureNotExists(validStudent, course);
        repository.add(validStudent.getId(), enr);
    }

    public void deleteEnrollment(Student student, Course course) {
        Enrollment enrollment = findOrThrow(student, course);
        repository.delete(student.getId(), enrollment);
    }

    public Enrollment findOrThrow(Student student, Course course) {
        if (student == null) {
            throw new DoesNotExist("Enrollment student");
        }
        if (course == null) {
            throw new DoesNotExist("Enrollment course");
        }

        for (Enrollment enr : repository.getAllByOwnerId(student.getId())) {
            if (student.equals(enr.getStudent()) && course.equals(enr.getCourse())) {
                return enr;
            }
        }
        throw new DoesNotExist("Enrollment (student=" + student + ", course=" + course + ")");
    }

    public Enrollment findOrThrow(int enrollmentId) {
        Enrollment enr = repository.getValueById(enrollmentId);

        if (enr == null) {
            throw new DoesNotExist("Enrollment with id " + enrollmentId);
        }

        return enr;
    }
    
    public Collection<Enrollment> getAll(){
    	return this.repository.getAll();
    }
    private void ensureNotExists(Student student, Course course) {
        for (Enrollment enr : repository.getAllByOwnerId(student.getId())) {
            if (student.equals(enr.getStudent()) && course.equals(enr.getCourse())) {
                throw new AlreadyExists("Enrollment (student=" + student + ", course=" + course + ")");
            }
        }
    }
    
    
   
}
