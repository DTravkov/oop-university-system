package application;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.User;
import model.enumeration.UIMessages;
import services.CourseService;
import services.EnrollmentService;
import services.LanguageService;
import services.UserService;
import utils.UIFields;

import java.util.Scanner;

import exceptions.ApplicationException;

public class EnrollmentApp {

    private static final EnrollmentService service = new EnrollmentService();
    private static final UserService userService = new UserService();
    private static final CourseService courseService = new CourseService();

    public static void startApp(Scanner scanner) {

        while (true) {
            printMenu();

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {

                    case "1":
                        createEnrollment(scanner);
                        break;

                    case "2":
                        deleteEnrollment(scanner);
                        break;

                    case "3":
                        getAllEnrollments(scanner);
                        break;

                    case "4":
                        getAllEnrollmentsOfStudent(scanner);
                        break;

                    case "5":
                        return;

                    default:
                        System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
                }

            } catch (ApplicationException e) {
                System.out.println(LanguageService.translate(e.getMessageKey(), e.getArgs()));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + LanguageService.translate(UIMessages.TITLE_ENROLL) + " ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.ENROLL));
        System.out.println("2. " + LanguageService.translate(UIMessages.DROP));
        System.out.println("3. " + LanguageService.translate(UIMessages.VIEW_ALL));
        System.out.println("4. " + LanguageService.translate(UIMessages.VIEW_STUDENT));
        System.out.println("5. " + LanguageService.translate(UIMessages.EXIT));
        System.out.println("--------------------");
    }

    private static void createEnrollment(Scanner scanner) {

        int studentId = UIFields.readInt(scanner, "createEnrollment", UIMessages.STUDENT_ID);
        int courseId = UIFields.readInt(scanner, "createEnrollment", UIMessages.COURSE_ID);

        User user = userService.get(studentId);
        if(!(user instanceof Student)){
            System.out.println("Impossible to enroll the person who is not a Student");
            return;
        }
        Student student = (Student) userService.get(studentId);
        Course course = courseService.get(courseId);

        service.createEnrollment(new Enrollment(course, student));

        System.out.println(LanguageService.translate(UIMessages.CREATED));
    }

    private static void getAllEnrollments(Scanner scanner) {
        System.out.println(service.getAll());
    }

    private static void getAllEnrollmentsOfStudent(Scanner scanner) {
        int studentId = UIFields.readInt(scanner, "createEnrollment", UIMessages.STUDENT_ID);

        System.out.println(
            service.getAll().stream()
                .filter(u -> u.getStudent() != null && u.getStudent().getId() == studentId)
                .toList()
        );
    }

    private static void deleteEnrollment(Scanner scanner) {

        int studentId = UIFields.readInt(scanner, "ENROLL_DROP", UIMessages.STUDENT_ID);
        int courseId = UIFields.readInt(scanner, "ENROLL_DROP", UIMessages.COURSE_ID);
        Student student = (Student) userService.get(studentId);
        Course course = courseService.get(courseId);

        service.deleteEnrollment(student, course);

        System.out.println(LanguageService.translate(UIMessages.DELETED));
    }
}
