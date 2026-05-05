package application;

import java.util.List;

import exceptions.ApplicationException;
import model.domain.*;
import model.dto.CourseDTO;
import model.enumeration.UIMessage;
import services.CourseService;
import services.EnrollmentService;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public final class EnrollmentApp extends BaseApp {

    private static final EnrollmentService enrollmentService = services.enrollmentService;
    private static final UserService userService = services.userService;
    private static final CourseService courseService = services.courseService;

    private EnrollmentApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 7);

            try {
                switch (choice) {
                    case "1":
                        createEnrollment();
                        break;
                    case "2":
                        deleteEnrollment();
                        break;
                    case "3":
                        printEnrollmentsOfStudent();
                        break;
                    case "4":
                        printEnrollmentsOfCourse();
                        break;
                    case "5":
                        increaseStudentPoints();
                        break;
                    case "6":
                        printAllEnrollments();
                        break;
                    case "7":
                        return;
                    default:
                        printInvalidChoice();
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    private static void printMenu() {
        println("\n|||  " + Translator.translate(UIMessage.MENU_TITLE_ENROLL) + " |||");
        println("1. " + Translator.translate(UIMessage.ENROLL_CREATE));
        println("2. " + Translator.translate(UIMessage.ENROLL_DROP));
        println("3. " + Translator.translate(UIMessage.ENROLL_VIEW_STUDENT));
        println("4. Get enrollments by course");
        println("5. Increase student points");
        println("6. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        println("7. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void createEnrollment() {
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        printCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        CourseDTO enrollCourseData = courseService.getDTO(courseId);

        println("Available Lecturers : ");
        println(enrollCourseData.getLectureTeachers().stream().map(dto -> dto.toShortString()).toList());
        int lecturerId = UIForms.readInt(scanner, UIMessage.INPUT_TEACHER_ID);

        println("Available Practice teachers : ");
        println(enrollCourseData.getPracticeTeachers().stream().map(dto -> dto.toShortString()).toList());
        int practiceId = UIForms.readInt(scanner, UIMessage.INPUT_TEACHER_ID);

        Enrollment created = enrollmentService.create(new Enrollment(courseId, studentId, lecturerId, practiceId));

        println(Translator.translate(UIMessage.MSG_CREATED));
        println(enrollmentService.getDTO(created));
    }

    private static void printEnrollmentsOfStudent() {
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        for (Enrollment e : enrollmentService.getAllByStudentId(studentId)) {
            println(enrollmentService.getDTO(e.getId()));
        }
    }

    private static void deleteEnrollment() {
        printAllEnrollments();
        int enrollmentId = UIForms.readInt(scanner, UIMessage.INPUT_ENROLLMENT_ID);
        enrollmentService.delete(enrollmentId);

        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void printEnrollmentsOfCourse() {
        printCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        for (Enrollment e : enrollmentService.getAllByCourseId(courseId)) {
            println(enrollmentService.getDTO(e.getId()).toShortString());
        }
    }

    private static void printAllEnrollments() {
        for (Enrollment e : enrollmentService.getAll()) {
            println(enrollmentService.getDTO(e.getId()).toShortString());
        }
    }

    private static void increaseStudentPoints() {
        printAllEnrollments();
        int enrollmentId = UIForms.readInt(scanner, UIMessage.INPUT_ENROLLMENT_ID);
        println("Choose point type:");
        println("1. First attestation");
        println("2. Second attestation");
        println("3. Final exam");
        int pointTypeChoice = Integer.parseInt(readChoice(UIMessage.MENU_CHOOSE, 1, 3));
        double pointsToAdd = UIForms.readDouble(scanner, UIMessage.INPUT_POINTS_TO_ADD);
        enrollmentService.increasePoints(enrollmentId, pointTypeChoice, pointsToAdd);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(enrollmentService.getDTO(enrollmentId));
    }

    private static void printStudents() {
        println("|||  Students |||");
        for (User user : userService.getAllByClassOrSubclass(Student.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }

    private static void printCourses() {
        println("|||  Courses |||");
        for (Course course : courseService.getAll()) {
            println(courseService.getDTO(course).toShortString());
        }
    }
}
