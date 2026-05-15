package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.Teacher;
import model.enumeration.TeacherType;

/**
 * TeacherService is a helper service that forwards read-style queries to course and enrollment services.
 * Those queries keep teacher-related application code smaller and easier to follow.
 */
public class TeacherService {

    private final CourseService courseSerivce;
    private final EnrollmentService enrollmentService;

    public TeacherService(CourseService courseService, EnrollmentService enrollmentService) {
        this.courseSerivce = courseService;
        this.enrollmentService = enrollmentService;
    }

    // QUERIES

    public Map<TeacherType, List<Course>> getCoursesByTeacher(Teacher teacher){
        return courseSerivce.getCoursesByTeacher(teacher);
    }

    public Map<Course, List<Enrollment>> getEnrollmentsByTeacher(Teacher teacher) {
        return enrollmentService.getEnrollmentsByTeacher(teacher);
    }

    public Map<Course, List<Student>> getStudentsByTeacher(Teacher teacher){
        Map<Course, List<Enrollment>> map = enrollmentService.getEnrollmentsByTeacher(teacher);
        Map<Course, List<Student>> studentMap = new HashMap<>();
        for(var entry : map.entrySet()){
            studentMap.computeIfAbsent(entry.getKey(),
            (k) -> entry.getValue().stream().map(e -> e.getStudent()).toList());
        }
        return studentMap;
    }

    public List<Student> getAllStudentsByTeacher(Teacher teacher){
        Map<Course,List<Student>> students = getStudentsByTeacher(teacher);
        List<Student> list = new ArrayList<>();
        for(var entry : students.entrySet()){
            entry.getValue().forEach(student -> list.add(student));
        }
        return list;
    }

}
