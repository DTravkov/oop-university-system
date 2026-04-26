package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.CourseType;
import model.enumeration.TeacherType;
import model.enumeration.UIMessages;
import model.factories.ServiceFactory;
import services.*;
import utils.UIFields;

public class CourseApp {

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final CourseService courseService = serviceFactory.getService(CourseService.class);
    private static final UserService userService = serviceFactory.getService(UserService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.MENU_CHOOSE, 1, 7);

            try {
                switch (choice) {
                    case "1":
                        createCourse(scanner);
                        break;
                    case "2":
                        getCourseById(scanner);
                        break;
                    case "3":
                        printCourses();
                        break;
                    case "4":
                        deleteCourse(scanner);
                        break;
                    case "5":
                        addTeacherToCourse(scanner);
                        break;
                    case "6":
                        getCourseTeacherList(scanner);
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
        System.out.println("\n--- Course App ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.COURSE_CREATE));
        System.out.println("2. Get course by id");
        System.out.println("3. " + LanguageService.translate(UIMessages.MENU_VIEW_ALL));
        System.out.println("4. Delete course");
        System.out.println("5. " + "Add teacher to a course");
        System.out.println("6. " + "Get list of teachers for a course");
        System.out.println("7. " + LanguageService.translate(UIMessages.MENU_EXIT));
    }

    private static void createCourse(Scanner scanner) {
        String name = UIFields.readNonEmpty(scanner, UIMessages.INPUT_COURSE_NAME);
        String description = UIFields.readNonEmpty(scanner, UIMessages.INPUT_COURSE_DESC);
        int credits = UIFields.readInt(scanner, UIMessages.INPUT_COURSE_CREDITS);
        CourseType type = askCourseType(scanner);

        Course course = new Course(name, description, credits, type);
        courseService.create(course);

        System.out.println(LanguageService.translate(UIMessages.MSG_CREATED));
        System.out.println("Created: " + course);
        System.out.println("All courses: " + courseService.getAll());
    }

    private static CourseType askCourseType(Scanner scanner) {
        while (true) {
            System.out.println(LanguageService.translate(UIMessages.INPUT_COURSE_TYPE));
            System.out.println("1. MAJOR");
            System.out.println("2. MINOR");
            System.out.println("3. ELECTIVE");
            String choice = UIFields.readChoice(scanner, UIMessages.MENU_CHOOSE, 1, 3);

            switch (choice) {
                case "1":
                    return CourseType.MAJOR;
                case "2":
                    return CourseType.MINOR;
                case "3":
                    return CourseType.ELECTIVE;
                default:
                    System.out.println(LanguageService.translate(UIMessages.MSG_INVALID_CHOICE));
            }
        }
    }

    private static void addTeacherToCourse(Scanner scanner) {
        printCourses();
        int courseId = UIFields.readInt(scanner, UIMessages.INPUT_COURSE_ID);
        printTeachers();
        int teacherId = UIFields.readInt(scanner, UIMessages.INPUT_TEACHER_ID);
        String teacherTypeId = UIFields.readChoice(scanner, UIMessages.INPUT_COURSE_TEACHER_TYPE, 1, 2);

        TeacherType type = teacherTypeId.equals("1")  ? TeacherType.LECTURE : TeacherType.PRACTICE;

        System.out.println(type);

        courseService.addTeacher(courseId, teacherId, type);

        System.out.println(courseService.get(courseId));
        System.out.println("Lecturer IDs : " + courseService.get(courseId).getLectureTeachers());
        System.out.println("Practice Teacher IDs : " + courseService.get(courseId).getPracticeTeachers());
    }

    private static void getCourseById(Scanner scanner) {
        printCourses();
        int id = UIFields.readInt(scanner, UIMessages.INPUT_COURSE_ID);
        System.out.println(courseService.get(id));
    }

    private static void getCourseTeacherList(Scanner scanner) {
        printCourses();
        int courseId = UIFields.readInt(scanner, UIMessages.INPUT_COURSE_ID);
        Course course = courseService.get(courseId);
        System.out.println("Lecturer IDs : " + course.getLectureTeachers());
        System.out.println("Practice Teacher IDs : " + course.getPracticeTeachers());
    }

    private static void printCourses() {
        System.out.println(courseService.getAll());
    }

    private static void printTeachers() {
        System.out.println("--- Lecturers ---");
        for(User u : userService.getAllByClass(Teacher.class)){
            Teacher t = (Teacher) u;
            if(t.isLecturer()){
                System.out.println(t);
            }
        }
        System.out.println("--- Practice teachers ---");
        for(User u : userService.getAllByClass(Teacher.class)){
            Teacher t = (Teacher) u;
            if(t.isPractice()){
                System.out.println(t);
            }
        }
        System.out.println("--- Both ---");
        for(User u : userService.getAllByClass(Teacher.class)){
            Teacher t = (Teacher) u;
            if(t.isLecturer() && t.isPractice()){
                System.out.println(t);
            }
        }
    }


    private static void deleteCourse(Scanner scanner) {
        printCourses();
        int id = UIFields.readInt(scanner, UIMessages.INPUT_COURSE_ID);
        courseService.delete(id);
        System.out.println(LanguageService.translate(UIMessages.MSG_DELETED));
        System.out.println(courseService.getAll());
    }

}
