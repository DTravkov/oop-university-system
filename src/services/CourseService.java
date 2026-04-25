package services;

import exceptions.AlreadyExists;
import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Teacher;
import model.domain.User;
import model.enumeration.TeacherType;
import model.repository.CourseRepository;
import services.events.Event;
import services.events.UserDeletedEvent;

public class CourseService extends BaseService<Course, CourseRepository>  {

    private final UserService userService;
    public CourseService() {
        super(CourseRepository.getInstance());
        this.userService = new UserService();
    }

    public void addTeacher(int courseId, int teacherId, TeacherType type){

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
            if(!checkedTeacher.isLecturer()){
                throw new OperationNotAllowed("adding non-lecturer as a lecture teacher to a course");
            }
            if(course.getLectureTeachers().contains(teacherId)){
                throw new AlreadyExists(" teacher " + teacherId + " as a lecturer");
            }
            course.addLectureTeacher(teacherId);
        }
        else if(type == TeacherType.PRACTICE){
            if(!checkedTeacher.isPractice()){
                throw new OperationNotAllowed("adding non-practice as a practice teacher to a course");
            }
            if(course.getPracticeTeachers().contains(teacherId)){
                throw new AlreadyExists(" teacher " + teacherId + " as a practice teacher");
            }
            course.addPracticeTeacher(teacherId);
        }else if(type == TeacherType.BOTH){
            throw new OperationNotAllowed(" passing 'BOTH' as a TeacherType");
        }

        repository.save(course);

    }

    @Override
    public void update(Event event){
        if(event instanceof UserDeletedEvent e){
            int deletedUserId = e.getUserId();
            for(Course c : this.getAll()){
                if(c.getPracticeTeachers().contains(deletedUserId)){
                    c.removePracticeTeacher(deletedUserId);
                    repository.save(c);
                }
                if(c.getLectureTeachers().contains(deletedUserId)){
                    c.removeLectureTeacher(deletedUserId);
                    repository.save(c);
                }
            }
            
        }
    }



}
