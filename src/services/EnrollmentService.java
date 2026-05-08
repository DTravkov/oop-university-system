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
import model.dto.CourseDTO;
import model.dto.EnrollmentDTO;
import model.enumeration.AttestationType;
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

        if(getStudentCredits(student.getId()) + course.getCredits() > 21){
            throw new OperationNotAllowed("having more than 21 credits total.");
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

    public int getStudentCredits(int studentId){
        User user = userService.get(studentId);
        if(!(user instanceof Student)){
            throw new OperationNotAllowed("viewing credits of a non-student account");
        }
        List<Enrollment> studentEnrollments = getAllByStudentId(studentId);
        int credits = 0;
        for(var enr : studentEnrollments){
            credits += courseService.get(enr.getCourseId()).getCredits();
        }
        return credits;
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
        CourseDTO course = courseService.getDTO(enrollment.getCourseId());
        return new EnrollmentDTO(
                enrollment,
                course,
                userService.getDTO(enrollment.getStudentId()),
                userService.getDTO(enrollment.getLectureTeacherId()),
                userService.getDTO(enrollment.getPracticeTeacherId())
        );
    }

    public void increasePoints(int enrollmentId, AttestationType type, double pointsToAdd) {
        FieldValidator.requirePositive(pointsToAdd, "Points increment");
        FieldValidator.requireNonNull(type, "Attestation type");

        Enrollment enrollment = this.get(enrollmentId);

        switch (type) {
            case FIRST_ATTESTATION:
                enrollment.setFirstAttestationPoint(enrollment.getFirstAttestationPoint() + pointsToAdd);
                break;
            case SECOND_ATTESTATION:
                enrollment.setSecondAttestationPoint(enrollment.getSecondAttestationPoint() + pointsToAdd);
                break;
            case FINAL_EXAM:
                enrollment.setFinalExamPoint(enrollment.getFinalExamPoint() + pointsToAdd);
                break;
            default:
                throw new OperationNotAllowed(" choosing invalid point type");
        }

        super.update(enrollment);
    }


    @Override
    public void subscribeToEvents(){
        
        eventSystem.subscribe(CourseDeleteEvent.class, event -> {
            cleanUpCourseEnrollments(event.getCourseId());
        });

        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            cleanUpUserEnrollments(event.getUserId());
        });
    }

    public void cleanUpCourseEnrollments(int courseId) {
        List<Enrollment> toDelete = this.getAllByCourseId(courseId);
        toDelete.forEach(this::delete);
    }

    public void cleanUpUserEnrollments(int deletedUserId) {
        User deletedUser = userService.get(deletedUserId);
        if (deletedUser instanceof Student) {
            List<Enrollment> toDelete = this.getAllByStudentId(deletedUserId);
            toDelete.forEach(this::delete);
            return;
        }
        if (deletedUser instanceof Teacher) {
            this.getAllByTeacherId(deletedUser.getId()).forEach(enr -> {
                if (enr.getLectureTeacherId() == deletedUserId) {
                    enr.setLectureTeacherId(AppSettings.DELETED_USER_ID);
                }
                if (enr.getPracticeTeacherId() == deletedUserId) {
                    enr.setPracticeTeacherId(AppSettings.DELETED_USER_ID);
                }
            });
            this.saveAll();
        }
    }


}
