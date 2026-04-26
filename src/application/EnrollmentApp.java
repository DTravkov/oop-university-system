package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.UIMessages;
import model.factories.ServiceFactory;
import services.*;
import utils.UIFields;

public class EnrollmentApp{

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final EnrollmentService enrollmentService = serviceFactory.getService(EnrollmentService.class);
    private static final UserService userService = serviceFactory.getService(UserService.class);
    private static final CourseService courseService = serviceFactory.getService(CourseService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.MENU_CHOOSE, 1, 7);

            try {
                switch (choice) {
                    case "1":
                        createEnrollment(scanner);
                        break;
                    case "2":
                        deleteEnrollment(scanner);
                        break;
                    case "3":
                        getEnrollmentsOfStudent(scanner);
                        break;
                    case "4":
                        getEnrollmentsOfCourse(scanner);
                        break;
                    case "5":
                        increaseStudentPoints(scanner);
                        break;
                    case "6":
                        getAllEnrollments();
                        break;
                    case "7":
                        return;
                    default:
                        System.out.println(LanguageService.translate(UIMessages.MSG_INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(LanguageService.translate(e.getMessageKey(), e.getArgs()));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + LanguageService.translate(UIMessages.MENU_TITLE_ENROLL) + " ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.ENROLL_CREATE));
        System.out.println("2. " + LanguageService.translate(UIMessages.ENROLL_DROP));
        System.out.println("3. " + LanguageService.translate(UIMessages.ENROLL_VIEW_STUDENT));
        System.out.println("4. Get enrollments by course");
        System.out.println("5. Increase student points");
        System.out.println("6. " + LanguageService.translate(UIMessages.MENU_VIEW_ALL));
        System.out.println("7. " + LanguageService.translate(UIMessages.MENU_EXIT));
    }

    private static void createEnrollment(Scanner scanner) {
        printStudents();
        printCourses();
        int studentId = UIFields.readInt(scanner, UIMessages.INPUT_STUDENT_ID);
        int courseId = UIFields.readInt(scanner, UIMessages.INPUT_COURSE_ID);

        Enrollment enrollment = new Enrollment(courseId, studentId);
        enrollmentService.create(enrollment);

        System.out.println(LanguageService.translate(UIMessages.MSG_CREATED));
        System.out.println(enrollment);
        System.out.println(enrollmentService.getAll());
    }

    private static void getEnrollmentsOfStudent(Scanner scanner) {
        printStudents();
        int studentId = UIFields.readInt(scanner, UIMessages.INPUT_STUDENT_ID);
        System.out.println(enrollmentService.getAllByStudentId(studentId));
    }

    private static void deleteEnrollment(Scanner scanner) {
        getAllEnrollments();
        int enrollmentId = UIFields.readInt(scanner, UIMessages.INPUT_ENROLLMENT_ID);
        enrollmentService.delete(enrollmentId);

        System.out.println(LanguageService.translate(UIMessages.MSG_DELETED));
    }

    private static void getEnrollmentsOfCourse(Scanner scanner) {
        printCourses();
        int courseId = UIFields.readInt(scanner, UIMessages.INPUT_COURSE_ID);
        System.out.println(enrollmentService.getAllByCourseId(courseId));
    }

    private static void getAllEnrollments() {
        System.out.println(enrollmentService.getAll());
    }

    private static void increaseStudentPoints(Scanner scanner) {
        getAllEnrollments();
        int enrollmentId = UIFields.readInt(scanner, UIMessages.INPUT_ENROLLMENT_ID);
        System.out.println("Choose point type:");
        System.out.println("1. First attestation");
        System.out.println("2. Second attestation");
        System.out.println("3. Final exam");
        int pointTypeChoice = Integer.parseInt(UIFields.readChoice(scanner, UIMessages.MENU_CHOOSE, 1, 3));
        double pointsToAdd = UIFields.readDouble(scanner, UIMessages.INPUT_POINTS_TO_ADD);
        enrollmentService.increasePoints(enrollmentId, pointTypeChoice, pointsToAdd);
        System.out.println(LanguageService.translate(UIMessages.MSG_CREATED));
        System.out.println(enrollmentService.get(enrollmentId));
    }

    private static void printStudents() {
        System.out.println("--- Students ---");
        for (User user : userService.getAllByClass(Student.class)) {
            Student student = (Student) user;
            System.out.println("ID: " + student.getId() + ", Name: " + student.getName() + ", Surname: " + student.getSurname());
        }
    }

    private static void printCourses() {
        System.out.println("--- Courses ---");
        for (Course course : courseService.getAll()) {
            System.out.println("ID: " + course.getId() + ", Name: " + course.getName());
        }
    }
}
