package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.SerializableModel;
import model.domain.Student;
import model.domain.Teacher;
import model.enumeration.AttestationType;
import model.enumeration.TeacherType;


public class TeacherService extends BaseService<SerializableModel>  {

    private final CourseService courseSerivce;
    private final EnrollmentService enrollmentService;

    public TeacherService(CourseService courseService, EnrollmentService enrollmentService) {
        super(SerializableModel.class);
        this.courseSerivce = courseService;
        this.enrollmentService = enrollmentService;
        subscribeToEvents();
    }

    public Map<TeacherType, List<Course>> getTeacherCourses(Teacher teacher){
        return courseSerivce.getAllByTeacher(teacher);
    }

    public Map<Course, List<Enrollment>> getTeacherEnrollments(Teacher teacher) {
        return enrollmentService.getTeacherEnrollments(teacher);
    }

    public Map<Course, List<Student>> getTeacherStudents(Teacher teacher){
        Map<Course, List<Enrollment>> map = enrollmentService.getTeacherEnrollments(teacher);
        Map<Course, List<Student>> studentMap = new HashMap<>();
        for(var entry : map.entrySet()){
            studentMap.computeIfAbsent(entry.getKey(),
            (k) -> entry.getValue().stream().map(e -> e.getStudent()).toList());
        }
        return studentMap;
    }

    public List<Student> getTeacherStudentsList(Teacher teacher){
        Map<Course,List<Student>> students = getTeacherStudents(teacher);
        List<Student> list = new ArrayList<>();
        for(var entry : students.entrySet()){
            entry.getValue().forEach(student -> list.add(student));
        }
        return list;
    }





}
