package application.apps;

import java.util.List;
import java.util.Map;

import model.domain.Course;
import model.domain.Dean;
import model.domain.Enrollment;
import model.domain.GraduateStudent;
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
        return new MenuBuilder(UIText.TEACHER_MENU_TITLE)
                .addAction(UIText.TEACHER_VIEW_COURSES, () -> printTeacherCourses())
                .addAction(UIText.TEACHER_VIEW_STUDENTS, () -> printTeacherStudents())
                .addAction(UIText.TEACHER_PUT_MARK, () -> putMarkToStudent())
                .addAction(UIText.TEACHER_COMPLAINT_MENU, () -> getComplaintMenu().start())
                .addAction(UIText.TEACHER_BECOME_SUPERVISOR, () -> becomeSupervisor())
                .addExit();
    }

    public static MenuBuilder getComplaintMenu() {
        MenuBuilder complaintMenu = new MenuBuilder(UIText.TEACHER_COMPLAINT_MENU);
        complaintMenu.addAction(UIText.TEACHER_VIEW_COMPLAINTS, () -> printTeacherComplaints());
        complaintMenu.addAction(UIText.TEACHER_SEND_COMPLAINT, () -> sendComplaint());
        complaintMenu.addAction(UIText.MENU_BACK, () -> complaintMenu.stop());
        return complaintMenu;
    }

    private static void putMarkToStudent() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<Course, List<Enrollment>> enrollmentMap = teacherService.getEnrollments(activeUser);
        List<Course> courses = enrollmentMap.keySet().stream().toList();
        if (enrollmentMap.isEmpty()) {
            printFail(UIText.MSG_NO_ENROLLMENTS);
            return;
        }

        printHeader(UIText.TEACHER_HEADER_COURSES);
        courses.forEach(c -> println(c.asLine()));
        Course course = UIForms.readIdFromList(scanner, UIText.INPUT_COURSE_ID, courses);
        List<Enrollment> courseEnrollments = enrollmentMap.get(course);
        printHeader(course.getName() + UIText.TEACHER_ENROLLMENTS_SUFFIX.localized());
        courseEnrollments.forEach(en -> println(en.asLine()));
        Enrollment enrollment = UIForms.readIdFromList(scanner, UIText.INPUT_ENROLLMENT_ID, courseEnrollments);
        AttestationType type = UIForms.readAttestationType(scanner);
        double pointsToAdd = UIForms.readDouble(scanner, UIText.INPUT_POINTS_TO_ADD);
        enrollmentService.addPoints(enrollment, activeUser, type, pointsToAdd);
        printSuccess(UIText.MSG_MARK_RECORDED);
    }

    private static void printTeacherStudents() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<Course, List<Student>> students = teacherService.getStudentsByTeacher(activeUser);
        printHeader(UIText.TEACHER_HEADER_STUDENTS);
        for (var entry : students.entrySet()) {
            printHeader(entry.getKey().getName());
            entry.getValue().forEach(s -> println(s.asLine()));
        }
    }

    private static void printTeacherCourses() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<TeacherType, List<Course>> courseMap = teacherService.getCourses(activeUser);
        for (var entry : courseMap.entrySet()) {
            printHeader(StringUtils.capitalize(entry.getKey().name()));
            entry.getValue().forEach(c -> println(c.asLine()));
        }
    }

    private static void sendComplaint() {
        Teacher teacher = (Teacher) getActiveUser();

        List<Dean> deans = userService.getUsersByClass(Dean.class);
        if (deans.isEmpty()) {
            println(UIText.MSG_NO_DEANS);
            return;
        }
        printHeader(UIText.TEACHER_HEADER_DEANS);
        deans.forEach(d -> println(d.asLine()));
        Dean dean = UIForms.readIdFromList(scanner, UIText.INPUT_RECEIVER_ID, deans);

        List<Student> students = userService.getUsersByClass(Student.class);
        if (students.isEmpty()) {
            println(UIText.MSG_NO_STUDENTS);
            return;
        }
        printHeader(UIText.TEACHER_HEADER_STUDENTS);
        students.forEach(s -> println(s.asLine()));
        Student student = UIForms.readIdFromList(scanner, UIText.INPUT_STUDENT_ID, students);

        ComplaintUrgencyLevel urgency = UIForms.readComplaintUrgencyLevel(scanner);
        String content = UIForms.readNonEmpty(scanner, UIText.INPUT_MESSAGE_CONTENT);

        TeacherComplaint complaint = new TeacherComplaint(urgency, teacher, dean, student, content);
        complaintService.create(complaint);
        printSuccess(UIText.MSG_COMPLAINT_SENT);
    }

    

    private static void printTeacherComplaints() {
        Teacher activeUser = (Teacher) getActiveUser();
        List<TeacherComplaint> complaints = complaintService.getComplaintsByTeacher(activeUser);
        if (complaints.isEmpty()) {
            printFail(UIText.MSG_NO_COMPLAINTS_YET);
            return;
        }
        complaints.forEach(tc -> println(tc.asLine()));
    }


    private static void becomeSupervisor(){
        Teacher teacher = (Teacher) getActiveUser();
        List<GraduateStudent> graduates = userService.getUsersByClass(GraduateStudent.class);
        if(graduates.isEmpty()){
            println(UIText.MSG_NO_GRADUATE_STUDENTS);
            return;
        }
        graduates.forEach(gs -> println(gs.asLine()));
        GraduateStudent graduate = UIForms.readIdFromList(scanner, UIText.INPUT_STUDENT_ID, graduates);
        teacherService.becomeSupervisor(graduate, teacher);
        printSuccess(UIText.TEACHER_SUCCESS_SUPERVISOR);
    }
}
