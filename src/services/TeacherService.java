package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.SerializableModel;
import model.domain.Student;
import model.domain.Teacher;
import model.enumeration.AttestationType;
import model.enumeration.TeacherType;


public class TeacherService extends BaseService<SerializableModel>  {

    private final CourseService courseSerivce;

    public TeacherService(CourseService courseService) {
        super(SerializableModel.class);
        this.courseSerivce = courseService;
        subscribeToEvents();
    }


    
    public void putMark(Enrollment enrollment, Teacher teacher, AttestationType att, double points) {
        if (enrollment.getLectureTeacher().getId() != teacher.getId()
                && enrollment.getPracticeTeacher().getId() != teacher.getId()) {
            throw new OperationNotAllowed("adding points to students led by other teacher");
        }
        switch (att) {
            case FIRST_ATTESTATION:
                enrollment.incrementFirstAttestationPoint(points);
                break;
            case SECOND_ATTESTATION:
                enrollment.incrementSecondAttestationPoint(points);
                break;
            case FINAL_EXAM:
                enrollment.incrementFinalExamPoint(points);
                break;
            default:
                break;
        }
        courseSerivce.update(enrollment.getCourse());
    }




    public Map<TeacherType, List<Course>> getTeacherCourses(Teacher teacher){
        return courseSerivce.getAllByTeacher(teacher);
    }

    public Map<Course, List<Student>> getTeacherStudents(Teacher teacher){
        Map<Course, List<Enrollment>> map = getTeacherEnrollments(teacher);
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

    public Map<Course, List<Enrollment>> getTeacherEnrollments(Teacher teacher){
        Map<TeacherType, List<Course>> map = courseSerivce.getAllByTeacher(teacher);
        Map<Course, List<Enrollment>> enrollments = new HashMap<>();
        for(var entry : map.entrySet()){
            for(Course c : entry.getValue()){
                enrollments.computeIfAbsent(c, (k) -> c.getEnrollments());
            }
        }
        return enrollments;
    }




}
