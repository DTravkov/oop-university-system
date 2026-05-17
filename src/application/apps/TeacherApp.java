package application.apps;

import java.util.List;
import java.util.Map;

import model.domain.Course;
import model.domain.Dean;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TeacherComplaint;
import model.enumeration.AttestationType;
import model.enumeration.ComplaintUrgencyLevel;
import model.enumeration.TeacherType;
import services.ComplaintService;
import services.EnrollmentService;
import services.TeacherService;
import services.UserService;
import utils.StringUtils;
import utils.UIForms;
import utils.UIText;

public final class TeacherApp extends BaseApp {

    static final UserService userService = services.userService;
    static final ComplaintService complaintService = services.complaintService;
    static final EnrollmentService enrollmentService = services.enrollmentService;
    static final TeacherService teacherService = services.teacherService;

    public TeacherApp() {
        super();
    }

    public static MenuBuilder getMenu() {
        return new MenuBuilder("Teacher Menu")
                .addAction("View my courses", () -> printTeacherCourses())
                .addAction("View my students", () -> printTeacherStudents())
                .addAction("Put a mark", () -> putMarkToStudent())
                .addAction("Complaint Menu", () -> getComplaintMenu().start())
                .addExit();
    }

    public static MenuBuilder getComplaintMenu() {
        MenuBuilder complaintMenu = new MenuBuilder("Complaint Menu");
        complaintMenu.addAction("View my complaints", () -> printTeacherComplaints());
        complaintMenu.addAction("Send new complaint", () -> sendComplaint());
        complaintMenu.addAction("Back", () -> complaintMenu.stop());
        return complaintMenu;
    }

    private static void putMarkToStudent() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<Course, List<Enrollment>> enrollmentMap = teacherService.getEnrollmentsByTeacher(activeUser);
        List<Course> courses = enrollmentMap.keySet().stream().toList();
        if (enrollmentMap.isEmpty()) {
            printFail("No enrollments");
            return;
        }

        printHeader("Courses");
        courses.forEach(c -> println(c.asLine()));
        Course course = UIForms.readIdFromList(scanner, UIText.INPUT_COURSE_ID, courses);
        List<Enrollment> courseEnrollments = enrollmentMap.get(course);
        printHeader(course.getName() + " Enrollments");
        courseEnrollments.forEach(en -> println(en.asLine()));
        Enrollment enrollment = UIForms.readIdFromList(scanner, UIText.INPUT_ENROLLMENT_ID, courseEnrollments);
        AttestationType type = UIForms.readAttestationType(scanner);
        double pointsToAdd = UIForms.readDouble(scanner, UIText.INPUT_POINTS_TO_ADD);
        enrollmentService.addPoints(enrollment, activeUser, type, pointsToAdd);
        printSuccess("Mark recorded.");
    }

    private static void printTeacherStudents() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<Course, List<Student>> students = teacherService.getStudentsByTeacher(activeUser);
        printHeader("Students");
        for (var entry : students.entrySet()) {
            printHeader(entry.getKey().getName());
            entry.getValue().forEach(s -> println(s.asLine()));
        }
    }

    private static void printTeacherCourses() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<TeacherType, List<Course>> courseMap = teacherService.getCoursesByTeacher(activeUser);
        for (var entry : courseMap.entrySet()) {
            printHeader(StringUtils.capitalize(entry.getKey().name()));
            entry.getValue().forEach(c -> println(c.asLine()));
        }
    }

    private static void sendComplaint() {
        Teacher teacher = (Teacher) getActiveUser();

        List<Dean> deans = userService.getUsersByClass(Dean.class);
        if (deans.isEmpty()) {
            println("No deans.");
            return;
        }
        printHeader("Deans");
        deans.forEach(d -> println(d.asLine()));
        Dean dean = UIForms.readIdFromList(scanner, UIText.INPUT_RECEIVER_ID, deans);

        List<Student> students = userService.getUsersByClass(Student.class);
        if (students.isEmpty()) {
            println("No students.");
            return;
        }
        printHeader("Students");
        students.forEach(s -> println(s.asLine()));
        Student student = UIForms.readIdFromList(scanner, UIText.INPUT_STUDENT_ID, students);

        ComplaintUrgencyLevel urgency = UIForms.readComplaintUrgencyLevel(scanner);
        String content = UIForms.readNonEmpty(scanner, UIText.INPUT_MESSAGE_CONTENT);

        TeacherComplaint complaint = new TeacherComplaint(urgency, teacher, dean, student, content);
        complaintService.create(complaint);
        printSuccess("Complaint sent.");
    }

    private static void printTeacherComplaints() {
        Teacher activeUser = (Teacher) getActiveUser();
        List<TeacherComplaint> complaints = complaintService.getComplaintsByTeacher(activeUser);
        if (complaints.isEmpty()) {
            printFail("You have no complaints yet,");
            return;
        }
        complaints.forEach(tc -> println(tc.asLine()));
    }
}
