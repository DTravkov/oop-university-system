package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.AlreadyExists;
import exceptions.OperationNotAllowed;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import model.enumeration.TeacherType;
import services.events.CourseDeleteEvent;
import services.events.UserDeleteEvent;

public class CourseService extends BaseService<Course>  {

    private final EnrollmentService enrollmentService;

    public CourseService(EnrollmentService enrollmentService) {
        super(Course.class);
        this.enrollmentService = enrollmentService;
        subscribeToEvents();
    }


    @Override
    public Course create(Course course) {
        if(existsByName(course.getName())){
            throw new AlreadyExists("course with the " +  course.getName() + " name");
        }
        return super.create(course);
    }

    @Override
    public void delete(int id) {
        eventSystem.publish(new CourseDeleteEvent(get(id)));
        super.delete(id);
    }

    public void addTeacher(Course course, Teacher teacher, TeacherType type){
        if(type == TeacherType.BOTH)
            throw new OperationNotAllowed(" passing 'BOTH' as a TeacherType");

        if(type == TeacherType.LECTURE){
            course.addLectureTeacher(teacher);
        }
        else if(type == TeacherType.PRACTICE){
            course.addPracticeTeacher(teacher);
        }

        this.update(course);
    }

    public void removeTeacher(Course course, Teacher teacher, TeacherType type){
        if(type == TeacherType.BOTH)
            throw new OperationNotAllowed(" passing 'BOTH' as a TeacherType");


        if(type == TeacherType.LECTURE){
            course.removeLectureTeacher(teacher);
        }
        else if(type == TeacherType.PRACTICE){
            course.removePracticeTeacher(teacher);
        }

        this.update(course);
    }


    public List<Enrollment> getStudentEnrollments(Student student) {
        return enrollmentService.getStudentEnrollments(student);
    }

    public Map<TeacherType, List<Course>> getAllByTeacher(Teacher teacher) {
        Map<TeacherType, List<Course>> map = new HashMap<>();
        map.put(TeacherType.LECTURE, new ArrayList<>());
        map.put(TeacherType.PRACTICE, new ArrayList<>());
        for(Course c : getAll()){
            if(c.getLectureTeachers().contains(teacher)){
                map.get(TeacherType.LECTURE).add(c);
            }
            if(c.getPracticeTeachers().contains(teacher)){
                map.get(TeacherType.PRACTICE).add(c);
            }
        }
        return map;
    }

    public boolean existsByName(String name){
        return repository.getAll()
                        .stream()
                        .anyMatch(c -> c.getName() == name);
    }

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> onUserDelete(event.getUser()));
    }

    public void onUserDelete(User user) {
        if(user instanceof Teacher  teacher){
            this.getAll().forEach(course -> {
                course.removePracticeTeacher(teacher);
                course.removeLectureTeacher(teacher);
            });
            repository.saveAll();
        }
    }




}
