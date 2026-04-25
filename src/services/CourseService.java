package services;

import exceptions.AlreadyExists;
import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Teacher;
import model.domain.User;
import model.enumeration.TeacherType;
import model.repository.CourseRepository;
import services.events.UserDeleteEvent;

public class CourseService extends BaseService<Course, CourseRepository>  {

    private final UserService userService;
    public CourseService(UserService userService) {
        super(CourseRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }


    @Override
    public Course create(Course course) {
        if(repository.existsByName(course.getName())){
            throw new AlreadyExists("course with the '" +  course.getName() + "' name");
        }
        return super.create(course);
    }

    public void addTeacher(int courseId, int teacherId, TeacherType type){
        if(type == TeacherType.BOTH)
            throw new OperationNotAllowed(" passing 'BOTH' as a TeacherType");
        
        Course course = this.get(courseId);
        User teacher = userService.get(teacherId);

        if(!teacher.getClass().equals(Teacher.class)){
            throw new OperationNotAllowed("adding non-teacher person as a teacher to a course");
        }

        Teacher checkedTeacher = (Teacher) teacher;

        if(checkedTeacher.getType() != TeacherType.BOTH && checkedTeacher.getType() != type){
            throw new OperationNotAllowed("adding a teacher to wrong position. (e.g lecturer as a practice teacher)");
        }

        if(type == TeacherType.LECTURE){
            if(course.getLectureTeachers().contains(teacherId)){
                throw new AlreadyExists(" teacher " + teacherId + " as a lecturer");
            }
            course.addLectureTeacher(teacherId);
        }
        else if(type == TeacherType.PRACTICE){
            if(course.getPracticeTeachers().contains(teacherId)){
                throw new AlreadyExists(" teacher " + teacherId + " as a practice teacher");
            }
            course.addPracticeTeacher(teacherId);
        }

        repository.save(course);

    }
    
    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            int deletedUserId = event.getUserId();
            this.getAll().forEach(course -> {
                course.removePracticeTeacher(deletedUserId);
                course.removeLectureTeacher(deletedUserId);
                repository.save(course);
            });
        });
    }
    



}
