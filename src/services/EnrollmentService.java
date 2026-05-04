package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import java.util.List;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.IEnrollable;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import model.dto.EnrollmentDTO;
import model.repository.EnrollmentRepository;
import services.events.CourseDeleteEvent;
import services.events.UserDeleteEvent;
import settings.AppSettings;
import utils.FieldValidator;

public class EnrollmentService extends BaseService<Enrollment, EnrollmentRepository>{

    private final UserService userService;
    private final CourseService courseService;

    public EnrollmentService(UserService userService, CourseService courseService) {
        super(EnrollmentRepository.getInstance());
        this.userService = userService;
        this.courseService = courseService;
        subscribeToEvents();
    }

   

    @Override
    public Enrollment create(Enrollment enrollment) {

        User student = userService.get(enrollment.getStudentId());
        Course course = courseService.get(enrollment.getCourseId());

        User lectureTeacher = userService.get(enrollment.getLectureTeacherId());
        User practiceTeacher = userService.get(enrollment.getPracticeTeacherId());
        
        if(!(student instanceof IEnrollable)){
            throw new OperationNotAllowed(" enrolling " + student.getClass().getSimpleName() + ". User id : "+ student.getId() + " can not be enrolled");
        }

        if(repository.exists(student.getId(), course.getId())){
            throw new AlreadyExists(" enrollment for student id " + student.getId() + " and course id " + course.getId());
        }

        if(!course.getLectureTeachers().contains(lectureTeacher.getId())){
            throw new DoesNotExist(" lecturer with id=" + lectureTeacher.getId() + " that teaches the id=" + course.getId() + " course" );
        }

        if(!course.getPracticeTeachers().contains(practiceTeacher.getId())){
            throw new DoesNotExist(" practice teacher with id=" + lectureTeacher.getId() + " that teaches the id=" + course.getId() + " course" );
        }

        if(!(lectureTeacher instanceof Teacher castedLecturer) || !castedLecturer.isLecturer()){
            throw new OperationNotAllowed(" assign non-lecturer as a lecture teacher.");
        }

        if(!(practiceTeacher instanceof Teacher castedPractice) || !castedPractice.isPractice()){
            throw new OperationNotAllowed(" assign non-practice teacher as a practice teacher.");
        }

        return super.create(enrollment);
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

    public List<Enrollment> getAllByTeacherId(int teacherId) {
        return repository.findAllByTeacherId(teacherId);
    }

    public EnrollmentDTO getDTO(int enrollmentId) {
        Enrollment enrollment = get(enrollmentId);
        return getDTO(enrollment);
    }

    public EnrollmentDTO getDTO(Enrollment enrollment) {
        Course course = courseService.get(enrollment.getCourseId());
        User student = userService.get(enrollment.getStudentId());
        User lectureTeacher = userService.get(enrollment.getLectureTeacherId());
        User practiceTeacher = userService.get(enrollment.getPracticeTeacherId());
        return new EnrollmentDTO(enrollment, course, student, lectureTeacher, practiceTeacher);
    }

    public void increasePoints(int enrollmentId, int pointTypeChoice, double pointsToAdd) {
        FieldValidator.requirePositive(pointsToAdd, "Points increment");

        Enrollment enrollment = this.get(enrollmentId);

        switch (pointTypeChoice) {
            case 1:
                enrollment.setFirstAttestationPoint(enrollment.getFirstAttestationPoint() + pointsToAdd);
                super.update(enrollment);
                break;
            case 2:
                enrollment.setSecondAttestationPoint(enrollment.getSecondAttestationPoint() + pointsToAdd);
                super.update(enrollment);
                break;
            case 3:
                enrollment.setFinalExamPoint(enrollment.getFinalExamPoint() + pointsToAdd);
                super.update(enrollment);
                break;
            default:
                throw new OperationNotAllowed(" choosing invalid point type");
        }

    }


    @Override
    public void subscribeToEvents(){
        
        eventSystem.subscribe(CourseDeleteEvent.class, event -> {
            int deletedId = event.getCourseId();
            this.getAllByCourseId(deletedId).forEach(enr -> this.delete(enr.getId()));
        });

        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            int deletedId = event.getUserId();
            User deletedUser = userService.get(deletedId);
            if(deletedUser instanceof Student){
                this.getAllByStudentId(deletedId).forEach(enr -> {
                    this.delete(enr.getId());
                });
            }
            else if(deletedUser instanceof Teacher){
                this.getAllByTeacherId(deletedUser.getId()).forEach(enr -> {
                    if(enr.getLectureTeacherId() == deletedId){
                        enr.setLectureTeacherId(AppSettings.DELETED_USER_ID);
                        this.update(enr);
                    }
                    if(enr.getPracticeTeacherId() == deletedId){
                        enr.setPracticeTeacherId(AppSettings.DELETED_USER_ID);
                        this.update(enr);
                    }
                
                });
            }
        });
    }


}
