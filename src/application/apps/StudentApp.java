package application.apps;

import java.util.List;

import model.domain.Enrollment;
import model.domain.GraduateStudent;
import model.domain.Student;
import model.domain.StudentOrganization;
import model.domain.Teacher;
import model.domain.User;
import services.EnrollmentService;
import services.StudentOrganizationService;
import services.TeacherService;
import services.UserService;
import utils.UIForms;
import utils.UIText;

public class StudentApp extends BaseApp {

    static final UserService userService = services.userService;
    static final EnrollmentService enrollmentService = services.enrollmentService;
    static final StudentOrganizationService organizationService = services.studentOrganizationService;
    static final TeacherService teacherService = services.teacherService;
    
    public StudentApp() {
        super();
    }

    public static MenuBuilder getMenu() {
        User user = getActiveUser();
        MenuBuilder menu = new MenuBuilder(UIText.STUDENT_MENU_TITLE)
        .addAction(UIText.STUDENT_TRANSCRIPT, () -> printTranscript())
        .addAction(UIText.STUDENT_ORG_MENU, () -> getOrgMenu().start());
        if(user instanceof GraduateStudent){
            menu.addAction(UIText.GRADUATE_CHECK_SUPERVISOR, () -> printSupervisor());
            menu.addAction(UIText.GRADUATE_REJECT_SUPERVISOR, () -> rejectSupervisor());
        }
        menu.addExit();
        return menu;
    }

    public static MenuBuilder getOrgMenu() {
        Student activeStudent = (Student) getActiveUser();

        boolean isOrganizationMember = organizationService.isMember(activeStudent);

        MenuBuilder menu = new MenuBuilder(UIText.STUDENT_ORG_MENU);
        menu.addAction(UIText.STUDENT_ORG_VIEW_ALL, () -> printAllStudentOrgs());
        menu.addAction(UIText.STUDENT_ORG_VIEW_DETAIL, () -> printOrgDetails());
        if (isOrganizationMember) {
            menu.addAction(UIText.STUDENT_ORG_VIEW_MINE, () -> {
                getMyOrgMenu().start();
                menu.stop();
                getOrgMenu().start();
            });
        } else {
            menu.addAction(UIText.STUDENT_ORG_JOIN, () -> {
                joinStudentOrg();
                menu.stop();
                getOrgMenu().start();
            });
            menu.addAction(UIText.STUDENT_ORG_CREATE, () -> {
                createStudentOrg();
                menu.stop();
                getOrgMenu().start();
            });
        }

        menu.addExit();
        return menu;
    }

    private static void printSupervisor(){
        GraduateStudent graduate = (GraduateStudent) getActiveUser();
        Teacher supervisor = graduate.getSupervisor();
        println(supervisor.asLine());
    }

    private static void rejectSupervisor(){
        GraduateStudent graduate = (GraduateStudent) getActiveUser();
        teacherService.deleteSupervisor(graduate);
        println(UIText.GRADUATE_SUPERVISOR_REJECTED);
    }

    private static void printOrgDetails(){
        List<StudentOrganization> allOrganizations = organizationService.getAll();
        if(allOrganizations.isEmpty()){
            println(UIText.MSG_NO_ORGANIZATIONS);
            return;
        }
        StudentOrganization pickedOrganization = UIForms.readIdFromList(scanner, UIText.INPUT_ORG_ID, allOrganizations);
        println(pickedOrganization.asTable());
    }

    private static MenuBuilder getMyOrgMenu() {
        Student activeStudent = (Student) getActiveUser();

        StudentOrganization org = organizationService.getOrganizationByMember(activeStudent);
        MenuBuilder menu = new MenuBuilder("");
        menu.addAction("["+org.getName()+"]", () -> println(org.asTable()));
        if (org.getPresident().equals(activeStudent)) {
            menu.addAction(UIText.STUDENT_ORG_ADD_MEMBER, () -> {
                addMemberToOrg(org);
                menu.stop();
                getMyOrgMenu().start();
            });
            menu.addAction(UIText.STUDENT_ORG_REMOVE_MEMBER, () -> {
                removeMemberFromOrg(org);
                menu.stop();
                getMyOrgMenu().start();
            });
            menu.addAction(UIText.STUDENT_ORG_DELETE, () -> {
                deleteStudentOrg(org);
                menu.stop();
            });
        } else {
            menu.addAction(UIText.STUDENT_ORG_LEAVE, () -> {
                leaveStudentOrg(org);
                menu.stop();
            });
        }
        menu.addAction(UIText.MENU_EXIT, () -> menu.stop());
        return menu;
    }


    private static void joinStudentOrg() {
        Student student = (Student) getActiveUser();
        List<StudentOrganization> orgs = organizationService.getAll();
        if (orgs.isEmpty()) {
            println(UIText.MSG_NO_ORGANIZATIONS);
            return;
        }
        StudentOrganization org = UIForms.readIdFromList(scanner, UIText.INPUT_ORG_ID, orgs);
        organizationService.addMember(org, student);
        printSuccess(UIText.MSG_JOINED_ORG, org.getName());
    }

    private static void createStudentOrg() {
        Student student = (Student) getActiveUser();
        String name = UIForms.readNonEmpty(scanner, UIText.INPUT_ORG_NAME);
        String description = UIForms.readNonEmpty(scanner, UIText.INPUT_ORG_DESC);
        StudentOrganization org = new StudentOrganization(name, description, student);
        organizationService.create(org);
        printSuccess(UIText.MSG_ORG_CREATED, name);
    }

    private static void printAllStudentOrgs() {
        List<StudentOrganization> orgs = organizationService.getAll();
        if (orgs.isEmpty()) {
            println(UIText.MSG_NO_ORGANIZATIONS);
            return;
        }
        printHeader(UIText.STUDENT_HEADER_ORGANIZATIONS);
        orgs.forEach(o -> println(o.asLine()));
    }

    private static void addMemberToOrg(StudentOrganization org) {
        List<Student> candidates = userService.getUsersByClass(Student.class).stream()
                .filter(s -> !organizationService.isMember(s))
                .toList();
        if (candidates.isEmpty()) {
            printFail(UIText.MSG_NO_STUDENTS_TO_ADD);
            return;
        }
        printHeader(UIText.MSG_STUDENTS_HEADER);
        candidates.forEach(s -> println(s.asLine()));
        Student student = UIForms.readIdFromList(scanner, UIText.INPUT_STUDENT_ID, candidates);
        organizationService.addMember(org, student);
        printSuccess(UIText.MSG_MEMBER_ADDED_TO, student.getFullname(), org.getName());
    }

    private static void removeMemberFromOrg(StudentOrganization org) {
        List<Student> removable = org.getMembers().stream()
                .filter(m -> !m.equals(org.getPresident()))
                .toList();
        if (removable.isEmpty()) {
            printFail(UIText.MSG_NO_MEMBERS_TO_REMOVE);
            return;
        }
        printHeader(UIText.STUDENT_HEADER_MEMBERS);
        removable.forEach(m -> println(m.asLine()));
        Student student = UIForms.readIdFromList(scanner, UIText.INPUT_STUDENT_ID, removable);
        organizationService.removeMember(org, student);
        printSuccess(UIText.MSG_MEMBER_REMOVED_FROM, student.getFullname(), org.getName());
    }

    private static void deleteStudentOrg(StudentOrganization org) {
        organizationService.delete(org);
        printSuccess(UIText.MSG_ORG_DELETED, org.getName());
    }

    private static void leaveStudentOrg(StudentOrganization org) {
        Student activeStudent = (Student) getActiveUser();
        organizationService.removeMember(org, activeStudent);
        printSuccess(UIText.MSG_LEFT_ORG, org.getName());
    }

    public static void printTranscript() {
        Student student = (Student) getActiveUser();
        List<Enrollment> enrollments = enrollmentService.getEnrollments(student);
        if (enrollments.isEmpty()) {
            printFail(UIText.MSG_NOT_ENROLLED);
            return;
        }
        println("\n" + student.asLine());
        println(UIText.MSG_GPA_OVERALL, enrollmentService.getGpaByStudent(student));
        println("--------------------------");
        for (Enrollment enr : enrollments) {
            println(enr.asTable());
            println("--------------------------");
        }
    }
}
