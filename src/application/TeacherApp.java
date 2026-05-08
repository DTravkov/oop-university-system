package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Dean;
import model.domain.Enrollment;
import model.domain.Teacher;
import model.domain.TeacherComplaint;
import model.domain.User;
import model.dto.CourseDTO;
import model.dto.EnrollmentDTO;
import model.dto.UserDTO;
import model.enumeration.AttestationType;
import model.enumeration.ComplaintUrgencyLevel;
import model.enumeration.UIMessage;
import services.ComplaintService;
import services.CourseService;
import services.EnrollmentService;
import services.UserService;
import utils.UIForms;

public final class TeacherApp extends BaseApp {

    private static final CourseService courseService = services.courseService;
    private static final EnrollmentService enrollmentService = services.enrollmentService;
    private static final UserService userService = services.userService;
    private static final ComplaintService complaintService = services.complaintService;

    private TeacherApp() {
    }

    public static void startApp() {
        User activeUser = getActiveUser();
        if (!(activeUser instanceof Teacher)) {
            throw new OperationNotAllowed("accessing Teacher Menu as a non-teacher user");
        }
        Teacher teacher = (Teacher) activeUser;

        ActionMenu menu = new ActionMenu("Teacher Menu");
        menu.addAction("View my courses", () -> handleExceptions(() -> viewMyCourses(teacher.getId())));
        menu.addAction("Increase student points", () -> handleExceptions(() -> increaseStudentPoints(teacher.getId())));
        menu.addAction("View my students", () -> handleExceptions(() -> viewMyStudents(teacher.getId())));
        menu.addAction("Send complaint to dean", () -> handleExceptions(() -> sendComplaintToDean(teacher.getId())));
        menu.addAction("Exit", menu::stop);
        menu.start();
    }

    private static void sendComplaintToDean(int teacherId) {
        List<User> deans = userService.getAllByClass(Dean.class);
        if (deans.isEmpty()) {
            printFail("No deans available to receive complaints.");
            return;
        }

        List<Integer> myStudentIds = enrollmentService.getAllByTeacherId(teacherId).stream()
                .map(Enrollment::getStudentId)
                .distinct()
                .toList();
        if (myStudentIds.isEmpty()) {
            printFail("You have no students to file a complaint about.");
            return;
        }

        println("\n||| Deans |||");
        deans.forEach(dean -> println(userService.getDTO(dean).toShortString()));
        int deanId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        if (deans.stream().noneMatch(dean -> dean.getId() == deanId)) {
            throw new OperationNotAllowed("sending complaint to a non-dean user");
        }

        println("\n||| My Students |||");
        myStudentIds.forEach(studentId -> println(userService.getDTO(studentId).toShortString()));
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        if (!myStudentIds.contains(studentId)) {
            throw new OperationNotAllowed("filing a complaint about a student not enrolled with you");
        }

        ComplaintUrgencyLevel urgency = UIForms.readComplaintUrgencyLevel(scanner);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

        TeacherComplaint complaint = new TeacherComplaint(urgency, teacherId, deanId, studentId, content);
        complaintService.sendComplaint(complaint);

        printSuccess("Complaint sent.");
        println(complaintService.getDTO(complaint));
    }

    private static List<Course> getCoursesOf(int teacherId) {
        return courseService.getAll().stream()
                .filter(course -> course.getLectureTeachers().contains(teacherId)
                        || course.getPracticeTeachers().contains(teacherId))
                .toList();
    }

    private static void viewMyCourses(int teacherId) {
        List<Course> courses = getCoursesOf(teacherId);
        if (courses.isEmpty()) {
            printFail("You have no assigned courses.");
            return;
        }

        println("\n||| My Courses |||");
        courses.forEach(course -> println(courseService.getDTO(course)));
    }

    private static void increaseStudentPoints(int teacherId) {
        List<Course> courses = getCoursesOf(teacherId);
        if (courses.isEmpty()) {
            printFail("You have no assigned courses.");
            return;
        }

        println("\n||| My Courses |||");
        courses.forEach(course -> println(courseService.getDTO(course).toShortString()));

        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        if (courses.stream().noneMatch(course -> course.getId() == courseId)) {
            throw new OperationNotAllowed("modifying a course you do not teach");
        }

        List<Enrollment> myEnrollments = enrollmentService.getAllByCourseId(courseId).stream()
                .filter(enr -> enr.getLectureTeacherId() == teacherId
                            || enr.getPracticeTeacherId() == teacherId).toList();

        if (myEnrollments.isEmpty()) {
            printFail("No students enrolled with you on this course.");
            return;
        }

        println("\n||| Student Scores |||");
        myEnrollments.forEach(enr -> println(enrollmentService.getDTO(enr).toShortString()));

        int enrollmentId = UIForms.readInt(scanner, UIMessage.INPUT_ENROLLMENT_ID);
        if (myEnrollments.stream().noneMatch(enr -> enr.getId() == enrollmentId)) {
            throw new OperationNotAllowed("modifying an enrollment you are not assigned to");
        }

        AttestationType pointType = UIForms.readAttestationType(scanner);
        double pointsToAdd = UIForms.readDouble(scanner, UIMessage.INPUT_POINTS_TO_ADD);
        enrollmentService.increasePoints(enrollmentId, pointType, pointsToAdd);

        printSuccess("Points updated.");
        println(enrollmentService.getDTO(enrollmentId));
    }

    private static void viewMyStudents(int teacherId) {
        List<Enrollment> enrollments = enrollmentService.getAllByTeacherId(teacherId);
        if (enrollments.isEmpty()) {
            printFail("You have no students.");
            return;
        }

        Map<CourseDTO, List<UserDTO>> courseMap = new HashMap<>();

        enrollments.stream()
                   .map(enrollmentService::getDTO)
                   .distinct()
                   .forEach(dto -> 
                        courseMap.computeIfAbsent(dto.getCourse(), k -> new ArrayList<>()).add(dto.getStudent())
                    );

        println("\n||| My Students |||");
        for(var entry : courseMap.entrySet()){
            println("|||" + entry.getKey().getName() +" |||");
            for(var studentDTO : entry.getValue()){
                println(studentDTO.toShortString());
            }
        }
    }
}
