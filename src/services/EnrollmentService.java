package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import java.util.Collection;

import services.events.Event;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.User;
import model.enumeration.UserRole;
import model.repository.EnrollmentRepository;
import services.events.UserDeletedEvent;

public class EnrollmentService extends BaseService<Enrollment, EnrollmentRepository>{

    private final UserService userService;
    private final CourseService courseService;

    public EnrollmentService() {
        super(EnrollmentRepository.getInstance());
        this.userService = new UserService();
        this.courseService = new CourseService();
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

    public Collection<Enrollment> getAllByStudentId(int studentId) {
        return repository.findAllByStudentId(studentId);
    }

    public Collection<Enrollment> getAllByCourseId(int courseId) {
        return repository.findAllByCourseId(courseId);
    }



    @Override
    public void update(Event e) {
        if(e instanceof UserDeletedEvent event){
            if(event.getUserClass().equals(Student.class)){
                for(Enrollment enr : repository.findAllByStudentId(event.getUserId())){
                    repository.delete(enr.getId());
                }
            }
        }
    }


}
