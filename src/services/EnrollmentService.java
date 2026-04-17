package services;

import java.util.Collection;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.Course;
import model.Enrollment;
import model.EnrollmentRepository;
import model.Student;
import model.Teacher;
import model.User;

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
        User student = userService.findOrThrow(enr.getStudentId());
        User lecturer = userService.findOrThrow(enr.getLecturerId());
        User proctor = userService.findOrThrow(enr.getPracticeId());
        
        if(! (student instanceof Student)) throw new OperationNotAllowed("create enrollment for a User who is not a student");
        if(! (lecturer instanceof Teacher) || ! ((Teacher) lecturer).isLecturer()) throw new OperationNotAllowed("create enrollment with invalid lecturer teacher");
        if(! (proctor instanceof Teacher) || !((Teacher) proctor).isPractice()) throw new OperationNotAllowed("create enrollment with invalid practice teacher");
        
        
        courseService.findOrThrow(enr.getCourseId());
        ensureNotExists(enr.getStudentId(), enr.getCourseId());

        repository.add(enr.getStudentId(), enr);
    }

    public void deleteEnrollment(int studentId, int courseId) {
        Enrollment enrollment = findOrThrow(studentId, courseId);
        repository.delete(studentId, enrollment);
    }

    public Enrollment findOrThrow(int studentId, int courseId) {
        for (Enrollment enr : repository.getAllByOwnerId(studentId)) {
            if (enr.getCourseId() == courseId) {
                return enr;
            }
        }
        throw new DoesNotExist("Enrollment (studentId=" + studentId + ", courseId=" + courseId + ")");
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
    private void ensureNotExists(int studentId, int courseId) {
        for (Enrollment enr : repository.getAllByOwnerId(studentId)) {
            if (enr.getCourseId() == courseId) {
                throw new AlreadyExists("Enrollment (studentId=" + studentId + ", courseId=" + courseId + ")");
            }
        }
    }
    
    
   
}
