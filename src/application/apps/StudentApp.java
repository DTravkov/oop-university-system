package application.apps;

import java.util.List;

import model.domain.Enrollment;
import model.domain.Student;
import model.domain.StudentOrganization;
import services.EnrollmentService;
import services.StudentOrganizationService;
import services.UserService;
import utils.UIForms;
import utils.UIText;

public class StudentApp extends BaseApp {

    static final UserService userService = services.userService;
    static final EnrollmentService enrollmentService = services.enrollmentService;
    static final StudentOrganizationService organizationService = services.studentOrganizationService;

    public StudentApp() {
        super();
    }

    public static MenuBuilder getMenu() {
        return new MenuBuilder("Student Menu")
                .addAction("My Transcript", () -> printTranscript())
                .addAction("Student Organization Menu", () -> getOrgMenu().start())
                .addExit();
    }

    public static MenuBuilder getOrgMenu() {
        Student activeStudent = (Student) getActiveUser();

        boolean isOrganizationMember = organizationService.isMember(activeStudent);

        MenuBuilder menu = new MenuBuilder("Student Organizations");
        menu.addAction("View all organizations", () -> printAllStudentOrgs());
        menu.addAction("View organization detail", () -> printAllStudentOrgs());
        if (isOrganizationMember) {
            menu.addAction("View my organization", () -> {
                getMyOrgMenu().start();
                menu.stop();
                getOrgMenu().start();
            });
        } else {
            menu.addAction("Join an organization", () -> {
                joinStudentOrg();
                menu.stop();
                getOrgMenu().start();
            });
            menu.addAction("Create an organization", () -> {
                createStudentOrg();
                menu.stop();
                getOrgMenu().start();
            });
        }

        menu.addExit();
        return menu;
    }

    private static MenuBuilder getMyOrgMenu() {
        Student activeStudent = (Student) getActiveUser();

        StudentOrganization org = organizationService.getOrganizationByMember(activeStudent);
        MenuBuilder menu = new MenuBuilder("");
        menu.addLabel(org.asTable());
        if (org.getPresident().equals(activeStudent)) {
            menu.addAction("Add member", () -> {
                addMemberToOrg(org);
                menu.stop();
                getMyOrgMenu().start();
            });
            menu.addAction("Remove member", () -> {
                removeMemberFromOrg(org);
                menu.stop();
                getMyOrgMenu().start();
            });
            menu.addAction("Delete the organization", () -> {
                deleteStudentOrg(org);
                menu.stop();
            });
        } else {
            menu.addAction("Leave organization", () -> {
                leaveStudentOrg(org);
                menu.stop();
            });
        }
        menu.addAction("Exit", () -> menu.stop());
        return menu;
    }

    private static void joinStudentOrg() {
        Student student = (Student) getActiveUser();
        List<StudentOrganization> orgs = organizationService.getAll();
        if (orgs.isEmpty()) {
            println("No organizations");
            return;
        }
        StudentOrganization org = UIForms.readIdFromList(scanner, UIText.INPUT_ORG_ID, orgs);
        organizationService.addMember(org, student);
        printSuccess("Joined " + org.getName() + ".");
    }

    private static void createStudentOrg() {
        Student student = (Student) getActiveUser();
        String name = UIForms.readNonEmpty(scanner, UIText.INPUT_ORG_NAME);
        String description = UIForms.readNonEmpty(scanner, UIText.INPUT_ORG_DESC);
        StudentOrganization org = new StudentOrganization(name, description, student);
        organizationService.create(org);
        printSuccess("Organization \"" + name + "\" created.");
    }

    private static void printAllStudentOrgs() {
        List<StudentOrganization> orgs = organizationService.getAll();
        if (orgs.isEmpty()) {
            println("No organizations");
            return;
        }
        printHeader("Organizations");
        orgs.forEach(o -> println(o.asLine()));
    }

    private static void addMemberToOrg(StudentOrganization org) {
        List<Student> candidates = userService.getUsersByClass(Student.class).stream()
                .filter(s -> !organizationService.isMember(s))
                .toList();
        if (candidates.isEmpty()) {
            printFail("No students available to add.");
            return;
        }
        printHeader("Students");
        candidates.forEach(s -> println(s.asLine()));
        Student student = UIForms.readIdFromList(scanner, UIText.INPUT_STUDENT_ID, candidates);
        organizationService.addMember(org, student);
        printSuccess(student.getFullname() + " added to " + org.getName() + ".");
    }

    private static void removeMemberFromOrg(StudentOrganization org) {
        List<Student> removable = org.getMembers().stream()
                .filter(m -> !m.equals(org.getPresident()))
                .toList();
        if (removable.isEmpty()) {
            printFail("No members to remove.");
            return;
        }
        printHeader("Members");
        removable.forEach(m -> println(m.asLine()));
        Student student = UIForms.readIdFromList(scanner, UIText.INPUT_STUDENT_ID, removable);
        organizationService.removeMember(org, student);
        printSuccess(student.getFullname() + " removed from " + org.getName() + ".");
    }

    private static void deleteStudentOrg(StudentOrganization org) {
        organizationService.delete(org);
        printSuccess("Organization \"" + org.getName() + "\" deleted.");
    }

    private static void leaveStudentOrg(StudentOrganization org) {
        Student activeStudent = (Student) getActiveUser();
        organizationService.removeMember(org, activeStudent);
        printSuccess("You left " + org.getName() + ".");
    }

    public static void printTranscript() {
        Student student = (Student) getActiveUser();
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
        if (enrollments.isEmpty()) {
            printFail("You are not enrolled on any course yet");
            return;
        }
        println("\n" + student.asLine());
        println("Overall GPA: " + enrollmentService.getGpaByStudent(student));
        println("--------------------------");
        for (Enrollment enr : enrollments) {
            println(enr.asTable());
            println("--------------------------");
        }
    }
}
