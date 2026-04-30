package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.UIMessage;
import model.factories.ServiceFactory;
import services.*;
import utils.Translator;
import utils.UIForms;

public class EnrollmentApp{

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final EnrollmentService enrollmentService = serviceFactory.getService(EnrollmentService.class);
    private static final UserService userService = serviceFactory.getService(UserService.class);
    private static final CourseService courseService = serviceFactory.getService(CourseService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 7);

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
                        System.out.println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + Translator.translate(UIMessage.MENU_TITLE_ENROLL) + " ---");
        System.out.println("1. " + Translator.translate(UIMessage.ENROLL_CREATE));
        System.out.println("2. " + Translator.translate(UIMessage.ENROLL_DROP));
        System.out.println("3. " + Translator.translate(UIMessage.ENROLL_VIEW_STUDENT));
        System.out.println("4. Get enrollments by course");
        System.out.println("5. Increase student points");
        System.out.println("6. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        System.out.println("7. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void createEnrollment(Scanner scanner) {
        printStudents();
        printCourses();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);

        Enrollment enrollment = new Enrollment(courseId, studentId);
        enrollmentService.create(enrollment);

        System.out.println(Translator.translate(UIMessage.MSG_CREATED));
        System.out.println(enrollment);
        System.out.println(enrollmentService.getAll());
    }

    private static void getEnrollmentsOfStudent(Scanner scanner) {
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        System.out.println(enrollmentService.getAllByStudentId(studentId));
    }

    private static void deleteEnrollment(Scanner scanner) {
        getAllEnrollments();
        int enrollmentId = UIForms.readInt(scanner, UIMessage.INPUT_ENROLLMENT_ID);
        enrollmentService.delete(enrollmentId);

        System.out.println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void getEnrollmentsOfCourse(Scanner scanner) {
        printCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        System.out.println(enrollmentService.getAllByCourseId(courseId));
    }

    private static void getAllEnrollments() {
        System.out.println(enrollmentService.getAll());
    }

    private static void increaseStudentPoints(Scanner scanner) {
        getAllEnrollments();
        int enrollmentId = UIForms.readInt(scanner, UIMessage.INPUT_ENROLLMENT_ID);
        System.out.println("Choose point type:");
        System.out.println("1. First attestation");
        System.out.println("2. Second attestation");
        System.out.println("3. Final exam");
        int pointTypeChoice = Integer.parseInt(UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 3));
        double pointsToAdd = UIForms.readDouble(scanner, UIMessage.INPUT_POINTS_TO_ADD);
        enrollmentService.increasePoints(enrollmentId, pointTypeChoice, pointsToAdd);
        System.out.println(Translator.translate(UIMessage.MSG_CREATED));
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
