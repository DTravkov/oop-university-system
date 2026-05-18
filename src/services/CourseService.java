package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.AlreadyExists;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import model.enumeration.TeacherType;
import services.events.concrete.CourseDeleteEvent;
import services.events.concrete.UserDeleteEvent;
import utils.Logger;

/**
 * CourseService is a concrete service. It implements logic for managing courses.
 */
public class CourseService extends GenericService<Course>  {

    private final EnrollmentService enrollmentService;

    public CourseService(EnrollmentService enrollmentService) {
        super(Course.class);
        this.enrollmentService = enrollmentService;
    }

    // CREATE / UPDATE / DELETE

    @Override
    public Course create(Course course) {
        if(existsByName(course.getName())){
            throw new AlreadyExists("A course named '" + course.getName() + "' already exists.");
        }
        return super.create(course);
    }

    @Override
    public void delete(Course course) {
        Course c = get(course);
        eventSystem.publish(new CourseDeleteEvent(c));
        super.delete(c);
    }

    public void addTeacher(Course course, Teacher teacher, TeacherType type){
        course.addTeacher(teacher, type);
        Logger.log("Add teacher (" + teacher.asLine() + ") to course (" + course.getId() + ") as (" + type + ")");
        repository.save(course);
    }

    public void removeTeacher(Course course, Teacher teacher, TeacherType type){
        course.removeTeacher(teacher, type);
        Logger.log("Remove teacher (" + teacher.asLine() + ") from course (" + course.getId() + ") as (" + type + ")");
        repository.save(course);
    }


    // QUERIES
    /**
     * finds all courses that teacher is lecturing or practicing, returns Map.
     * @param teacher
     * @return
     */
    public Map<TeacherType, List<Course>> getCourses(Teacher teacher) {
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
        return find(c -> c.getName().equals(name)) != null;
    }


    // EVENT HANDLING

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> onUserDelete(event.getUser()));
    }

    public void onUserDelete(User user) {
        if(user instanceof Teacher  teacher){
            getAll().forEach(course -> course.detachTeacher(teacher));
            repository.saveAll();
        }
    }




}
