package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.GraduateStudent;
import model.domain.SerializableModel;
import model.domain.Student;
import model.domain.Teacher;
import model.enumeration.TeacherType;
import utils.UIText;

/**
 * TeacherService is a helper service that forwards read-style queries to course and enrollment services.
 * Those queries keep teacher-related application code smaller and easier to follow.
 */
public class TeacherService extends BaseService<SerializableModel> {

    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final UserService userService;

    public TeacherService(CourseService courseService, EnrollmentService enrollmentService, UserService userService) {
        //null here because there is no object associated with TeacherService,
        // as teacher service is helper
        super(null);
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.userService = userService;
    }

    public void becomeSupervisor(GraduateStudent student, Teacher teacher){
        if(student.getSupervisor() != null){
            throw new OperationNotAllowed(UIText.GRADUATE_HAS_SUPERVISOR);
        }
        student.setSupervisor(teacher);
        userService.update(student);
        return;
    }
    public void deleteSupervisor(GraduateStudent student){
        if(student.getSupervisor() == null){
            throw new OperationNotAllowed(UIText.GRADUATE_NO_SUPERVISOR);
        }
        student.setSupervisor(null);
        userService.update(student);
        return;
    }

    // QUERIES

    public Map<TeacherType, List<Course>> getCourses(Teacher teacher){
        return courseService.getCourses(teacher);
    }

    public Map<Course, List<Enrollment>> getEnrollments(Teacher teacher) {
        return enrollmentService.getEnrollments(teacher);
    }
    /**
     * returns Map of {@link Course} to {@link List} of students, that teacher is teaching.
     * @param teacher
     * @return
     */
    public Map<Course, List<Student>> getStudentsByTeacher(Teacher teacher){
        Map<Course, List<Enrollment>> map = enrollmentService.getEnrollments(teacher);
        Map<Course, List<Student>> studentMap = new HashMap<>();
        for(var entry : map.entrySet()){
            studentMap.computeIfAbsent(entry.getKey(),
            (k) -> entry.getValue().stream().map(e -> e.getStudent()).toList());
        }
        return studentMap;
    }

    /**
     * returns list of all students teacher is teaching
     * @param teacher
     * @return
     */
    public List<Student> getAllStudentsByTeacher(Teacher teacher){
        Map<Course,List<Student>> students = getStudentsByTeacher(teacher);
        List<Student> list = new ArrayList<>();
        for(var entry : students.entrySet()){
            entry.getValue().forEach(student -> list.add(student));
        }
        return list;
    }

}
