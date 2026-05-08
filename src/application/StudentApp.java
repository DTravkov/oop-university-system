package application;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.StudentOrganization;
import model.dto.CourseDTO;
import model.enumeration.UIMessage;
import services.CourseService;
import services.EnrollmentService;
import services.StudentOrganizationService;
import services.UserService;
import utils.UIForms;

public final class StudentApp extends BaseApp {

    private static final CourseService courseService = services.courseService;
    private static final EnrollmentService enrollmentService = services.enrollmentService;
    private static final StudentOrganizationService organizationService = services.studentOrganizationService;
    private static final UserService userService = services.userService;

    private StudentApp() {
    }

    public static void startApp() {
        if (!(getActiveUser() instanceof Student)) {
            throw new OperationNotAllowed("accessing Student Menu as a non-student user");
        }

        ActionMenu menu = new ActionMenu("Student Menu");
        menu.addAction("Education Management", StudentApp::startEducationMenu);
        menu.addAction("Student Organizations", StudentApp::startOrganizationsMenu);
        menu.addAction("Exit", menu::stop);
        menu.start();
    }

    private static void startEducationMenu() {
        ActionMenu menu = new ActionMenu("Education Management");
        menu.addAction("View all courses", () -> handleExceptions(StudentApp::printAllCourses));
        menu.addAction("View course details", () -> handleExceptions(StudentApp::printCourseDetails));
        menu.addAction("Enroll on a course", () -> handleExceptions(StudentApp::enrollOnCourse));
        menu.addAction("My transcript", () -> handleExceptions(StudentApp::printTranscript));
        menu.addAction("Back", menu::stop);
        menu.start();
    }

    private static void printAllCourses() {
        List<Course> courses = courseService.getAll();
        if (courses.isEmpty()) {
            printFail("No courses found.");
            return;
        }
        println("\n||| Courses |||");
        courses.forEach(course -> println(courseService.getDTO(course).toShortString()));
    }

    private static void printCourseDetails() {
        printAllCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        println(courseService.getDTO(courseId));
    }

    private static void enrollOnCourse() {
        printAllCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        CourseDTO courseDTO = courseService.getDTO(courseId);

        if (courseDTO.getLectureTeachers().isEmpty() || courseDTO.getPracticeTeachers().isEmpty()) {
            printFail("Course is missing lecture or practice teachers; cannot enroll yet.");
            return;
        }

        println("\n||| Available lecturers |||");
        courseDTO.getLectureTeachers().forEach(t -> println(t.toShortString()));
        int lectureTeacherId = UIForms.readInt(scanner, UIMessage.INPUT_LECTURER_ID);

        println("\n||| Available practice teachers |||");
        courseDTO.getPracticeTeachers().forEach(t -> println(t.toShortString()));
        int practiceTeacherId = UIForms.readInt(scanner, UIMessage.INPUT_PRACTICE_ID);

        int studentId = getActiveUser().getId();
        Enrollment created = enrollmentService.create(
                new Enrollment(courseId, studentId, lectureTeacherId, practiceTeacherId));

        printSuccess("Enrolled successfully.");
        println(enrollmentService.getDTO(created));
    }

    private static void printTranscript() {
        int studentId = getActiveUser().getId();
        List<Enrollment> enrollments = enrollmentService.getAllByStudentId(studentId);

        if (enrollments.isEmpty()) {
            printFail("You have no enrollments yet.");
            return;
        }

        println("\n||| Transcript for " + getActiveUser().getFullName() + " |||");
        enrollments.forEach(enr -> println(enrollmentService.getDTO(enr)));

        double overallGpa = enrollments.stream().mapToDouble(Enrollment::getGpa).sum() / enrollments.size();
        overallGpa = Math.round(overallGpa * 100.0) / 100.0;

        println("\n||| Overall |||");
        println("Total enrollments: " + enrollments.size());
        println("Cumulative GPA: " + overallGpa);
    }


    private static void startOrganizationsMenu() {
        ActionMenu menu = new ActionMenu("Student Organizations");
        menu.addAction("View all organizations", () -> handleExceptions(StudentApp::printAllOrganizations));
        menu.addAction("View organization details", () -> handleExceptions(StudentApp::printOrganizationDetails));
        menu.addAction("Create my organization", () -> handleExceptions(StudentApp::createMyOrganization));
        menu.addAction("Manage my organization", () -> handleExceptions(StudentApp::startManageMyOrganizationMenu));
        menu.addAction("Back", menu::stop);
        menu.start();
    }

    private static void printAllOrganizations() {
        List<StudentOrganization> orgs = organizationService.getAll();
        if (orgs.isEmpty()) {
            printFail("No student organizations yet.");
            return;
        }
        println("\n||| Student organizations |||");
        orgs.forEach(org -> println(organizationService.getDTO(org).toShortString()));
    }

    private static void printOrganizationDetails() {
        printAllOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        println(organizationService.getDTO(organizationId));
    }

    private static void createMyOrganization() {
        String name = UIForms.readNonEmpty(scanner, "Organization name: ");
        String description = UIForms.readNonEmpty(scanner, "Organization description: ");
        int presidentId = getActiveUser().getId();

        StudentOrganization created = organizationService.create(
                new StudentOrganization(name, description, presidentId));

        printSuccess("Organization created.");
        println(organizationService.getDTO(created));
    }

    private static void startManageMyOrganizationMenu() {
        StudentOrganization myOrganization = getMyOrganization();

        ActionMenu menu = new ActionMenu("Manage '" + myOrganization.getName() + "'");
        menu.addAction("View details", () -> handleExceptions(() -> println(organizationService.getDTO(getMyOrganization()))));
        menu.addAction("Add member", () -> handleExceptions(() -> addMemberToOrganization(getMyOrganization())));
        menu.addAction("Set new president", () -> handleExceptions(() -> changePresident(getMyOrganization())));
        menu.addAction("Remove member", () -> handleExceptions(() -> removeMemberFromOrganization(getMyOrganization())));
        menu.addAction("Delete organization", () -> handleExceptions(() -> deleteMyOrganization(getMyOrganization(), menu)));
        menu.addAction("Back", menu::stop);
        menu.start();
    }

    private static void addMemberToOrganization(StudentOrganization organization) {
        printAllStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        organizationService.addMember(organization.getId(), studentId);
        printSuccess("Member added.");
        println(organizationService.getDTO(organization.getId()));
    }

    private static void removeMemberFromOrganization(StudentOrganization organization) {
        if (organization.getMembers().size() <= 1) {
            printFail("Organization has no removable members.");
            return;
        }

        println("\n||| Current members |||");
        organization.getMembers().stream()
                .filter(memberId -> memberId != organization.getPresidentId())
                .forEach(memberId -> println(userService.getDTO(memberId).toShortString()));

        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        organizationService.removeMember(organization.getId(), studentId);

        printSuccess("Member removed.");
        println(organizationService.getDTO(organization.getId()));
    }

    private static void changePresident(StudentOrganization organization) {
        printAllStudents();
        int newPresidentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        organizationService.setPresident(organization.getId(), newPresidentId);
        printSuccess("New president assigned");
    }

    private static void deleteMyOrganization(StudentOrganization organization, ActionMenu menu) {
        organizationService.delete(organization.getId());
        printSuccess("Organization deleted.");
        menu.stop();
    }

    private static StudentOrganization getMyOrganization() {
        StudentOrganization organization = organizationService.getByPresidentId(getActiveUser().getId());
        if (organization == null) {
            throw new OperationNotAllowed("managing an organization where you are not the president");
        }
        return organization;
    }

    private static void printAllStudents() {
        println("\n||| Students |||");
        userService.getAllByClassOrSubclass(Student.class)
                .forEach(student -> println(userService.getDTO(student).toShortString()));
    }
}
