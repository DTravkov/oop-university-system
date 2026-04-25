package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import java.util.List;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.User;
import model.enumeration.UserRole;
import model.factories.ServiceFactory;
import model.repository.EnrollmentRepository;
import services.events.UserDeleteEvent;

public class EnrollmentService extends BaseService<Enrollment, EnrollmentRepository>{

    private final UserService userService;
    private final CourseService courseService;

    public EnrollmentService() {
        super(EnrollmentRepository.getInstance());
        this.userService = ServiceFactory.getInstance().getService(UserService.class);
        this.courseService = ServiceFactory.getInstance().getService(CourseService.class);
        subscribeToEvents();
    }

   

    @Override
    public void create(Enrollment enrollment) {
        User student = userService.get(enrollment.getStudentId());
        Course course = courseService.get(enrollment.getCourseId());

        if(repository.exists(student.getId(), course.getId())){
            throw new AlreadyExists(" enrollment for student id " + student.getId() + " and course id " + course.getId());
        }

        if(UserRole.fromUser(student) != UserRole.STUDENT){
            throw new OperationNotAllowed(" enrolling the user, who is not a student. user id : "+ student.getId());
        }

        enrollment.setStudentId(student.getId());
        enrollment.setCourseId(course.getId());

        repository.save(enrollment);
    }

    public Enrollment getByStudentIdAndCourseId(int studentId, int courseId) {
        Enrollment enrollment = repository.findByStudentIdAndCourseId(studentId, courseId);
        if (enrollment == null) {
            throw new DoesNotExist("Enrollment for student id " + studentId + " and course id " + courseId);
        }
        return enrollment;
    }

    public List<Enrollment> getAllByStudentId(int studentId) {
        return repository.findAllByStudentId(studentId);
    }

    public List<Enrollment> getAllByCourseId(int courseId) {
        return repository.findAllByCourseId(courseId);
    }

    public void increasePoints(int enrollmentId, int pointTypeChoice, double pointsToAdd) {
        if (pointsToAdd <= 0) {
            throw new OperationNotAllowed(" increasing enrollment points with non-positive value");
        }

        Enrollment enrollment = this.get(enrollmentId);

        switch (pointTypeChoice) {
            case 1:
                enrollment.setFirstAttestationPoint(enrollment.getFirstAttestationPoint() + pointsToAdd);
                break;
            case 2:
                enrollment.setSecondAttestationPoint(enrollment.getSecondAttestationPoint() + pointsToAdd);
                break;
            case 3:
                enrollment.setFinalExamPoint(enrollment.getFinalExamPoint() + pointsToAdd);
                break;
            default:
                throw new OperationNotAllowed(" choosing invalid point type");
        }

        repository.save(enrollment);
    }


    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            int deletedUserId = event.getUserId();
            this.getAllByStudentId(deletedUserId).forEach(enr -> {
                repository.delete(enr.getId());
            });
        });
    }


}
