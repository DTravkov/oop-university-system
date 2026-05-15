package application;

import java.util.List;
import java.util.Map;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.Teacher;
import model.enumeration.AttestationType;
import model.enumeration.TeacherType;
import model.enumeration.UIMessage;
import services.EnrollmentService;
import services.TeacherService;
import utils.StringUtils;
import utils.UIForms;

public class TeacherMenus extends BaseApp {

    static final EnrollmentService enrollmentService = services.enrollmentService;
    static final TeacherService teacherService = services.teacherService;


    static MenuBuilder getTeacherMenu() {
        MenuBuilder menu = new MenuBuilder("Teacher Menu");
        menu.addAction("View my courses", () -> printTeacherCourses());
        menu.addAction("View my students", () -> printTeacherStudents());
        menu.addAction("Put a mark", () -> putMarkToStudent());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void putMarkToStudent() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<Course,List<Enrollment>> enrollmentMap = teacherService.getEnrollmentsByTeacher(activeUser);
        List<Course> courses = enrollmentMap.keySet().stream().toList();
        if(enrollmentMap.isEmpty()){
            printFail("No enrollments");
            return;
        }

        printHeader("Courses");
        courses.forEach(c -> println(c.asLine()));
        Course course = UIForms.readIdFromList(scanner, UIMessage.INPUT_COURSE_ID, courses);
        List<Enrollment> courseEnrollments = enrollmentMap.get(course);
        printHeader(course.getName() + " Enrollments");
        courseEnrollments.forEach(en -> println(en.asLine()));
        Enrollment enrollment = UIForms.readIdFromList(scanner, UIMessage.INPUT_ENROLLMENT_ID, courseEnrollments);
        AttestationType type = UIForms.readAttestationType(scanner);
        double pointsToAdd = UIForms.readDouble(scanner, UIMessage.INPUT_POINTS_TO_ADD);
        enrollmentService.addPoints(enrollment, activeUser, type, pointsToAdd);
        printSuccess("Mark recorded.");
    }

    private static void printTeacherStudents() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<Course,List<Student>> students = teacherService.getStudentsByTeacher(activeUser);
        printHeader("Students");
        for(var entry : students.entrySet()){
            printHeader(entry.getKey().getName());
            entry.getValue().forEach(s -> println(s.asLine()));
        }
    }

    private static void printTeacherCourses() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<TeacherType, List<Course>> courseMap = teacherService.getCoursesByTeacher(activeUser);
        for(var entry : courseMap.entrySet()){
            printHeader(StringUtils.capitalize(entry.getKey().name()));
            entry.getValue().forEach(c -> println(c.asLine()));
        }
    }
}
