package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.Course;
import model.enumeration.CourseType;
import model.enumeration.UIMessages;
import services.CourseService;
import services.LanguageService;
import utils.UIFields;

public class CourseApp {

    private static final CourseService courseService = new CourseService();

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.CHOOSE, 1, 5);

            try {
                switch (choice) {
                    case "1":
                        createCourse(scanner);
                        break;
                    case "2":
                        getCourseById(scanner);
                        break;
                    case "3":
                        getAllCourses();
                        break;
                    case "4":
                        deleteCourse(scanner);
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
        System.out.println("\n--- Course App ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.CREATE_COURSE));
        System.out.println("2. Get course by id");
        System.out.println("3. " + LanguageService.translate(UIMessages.VIEW_ALL));
        System.out.println("4. Delete course");
        System.out.println("5. " + LanguageService.translate(UIMessages.EXIT));
    }

    private static void createCourse(Scanner scanner) {
        String name = UIFields.readNonEmpty(scanner, UIMessages.COURSE_NAME);
        String description = UIFields.readNonEmpty(scanner, UIMessages.COURSE_DESC);
        int credits = UIFields.readInt(scanner, UIMessages.COURSE_CREDITS);
        CourseType type = askCourseType(scanner);

        Course course = new Course(name, description, credits, type);
        courseService.create(course);

        System.out.println(LanguageService.translate(UIMessages.CREATED));
        System.out.println("Created: " + course);
        System.out.println("All courses: " + courseService.getAll());
    }

    private static CourseType askCourseType(Scanner scanner) {
        while (true) {
            System.out.println(LanguageService.translate(UIMessages.COURSE_TYPE));
            System.out.println("1. MAJOR");
            System.out.println("2. MINOR");
            System.out.println("3. ELECTIVE");
            String choice = UIFields.readChoice(scanner, UIMessages.CHOOSE, 1, 3);

            switch (choice) {
                case "1":
                    return CourseType.MAJOR;
                case "2":
                    return CourseType.MINOR;
                case "3":
                    return CourseType.ELECTIVE;
                default:
                    System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
            }
        }
    }

    private static void getCourseById(Scanner scanner) {
        int id = UIFields.readInt(scanner, UIMessages.COURSE_ID);
        System.out.println(courseService.get(id));
    }

    private static void getAllCourses() {
        System.out.println(courseService.getAll());
    }

    private static void deleteCourse(Scanner scanner) {
        int id = UIFields.readInt(scanner, UIMessages.COURSE_ID);
        courseService.delete(id);
        System.out.println(LanguageService.translate(UIMessages.DELETED));
        System.out.println(courseService.getAll());
    }

}
