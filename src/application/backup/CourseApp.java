package application;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.CourseType;
import model.enumeration.TeacherType;
import model.enumeration.UIMessage;
import services.CourseService;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public final class CourseApp extends BaseApp {

    private static final CourseService courseService = services.courseService;
    private static final UserService userService = services.userService;

    private CourseApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 7);

            try {
                switch (choice) {
                    case "1":
                        createCourse();
                        break;
                    case "2":
                        printCourseById();
                        break;
                    case "3":
                        printAllCourses();
                        break;
                    case "4":
                        deleteCourse();
                        break;
                    case "5":
                        addTeacherToCourse();
                        break;
                    case "6":
                        printCourseTeacherList();
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
        println("\n|||  Course App |||");
        println("1. " + Translator.translate(UIMessage.COURSE_CREATE));
        println("2. Get course by id");
        println("3. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        println("4. Delete course");
        println("5. " + "Add teacher to a course");
        println("6. " + "Get list of teachers for a course");
        println("7. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void createCourse() {
        String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_COURSE_NAME);
        String description = UIForms.readNonEmpty(scanner, UIMessage.INPUT_COURSE_DESC);
        int credits = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_CREDITS);
        CourseType type = askCourseType();

        Course course = new Course(name, description, credits, type);
        courseService.create(course);

        println(Translator.translate(UIMessage.MSG_CREATED));
        println(courseService.getDTO(course.getId()));
    }

    private static CourseType askCourseType() {
        while (true) {
            println(Translator.translate(UIMessage.INPUT_COURSE_TYPE));
            println("1. MAJOR");
            println("2. MINOR");
            println("3. ELECTIVE");
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 3);

            switch (choice) {
                case "1":
                    return CourseType.MAJOR;
                case "2":
                    return CourseType.MINOR;
                case "3":
                    return CourseType.ELECTIVE;
                default:
                    printInvalidChoice();
            }
        }
    }

    private static void addTeacherToCourse() {
        printAllCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        printTeachers();
        int teacherId = UIForms.readInt(scanner, UIMessage.INPUT_TEACHER_ID);
        String teacherTypeId = readChoice(UIMessage.INPUT_COURSE_TEACHER_TYPE, 1, 2);

        TeacherType type = teacherTypeId.equals("1") ? TeacherType.LECTURE : TeacherType.PRACTICE;

        courseService.addTeacher(courseId, teacherId, type);

        println(courseService.getDTO(courseId));
    }

    private static void printCourseById() {
        printAllCourses();
        int id = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        println(courseService.getDTO(id));
    }

    private static void printCourseTeacherList() {
        printAllCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        Course course = courseService.get(courseId);
        println(courseService.getDTO(course.getId()));
    }

    private static void printAllCourses() {
        println("|||  Courses |||");
        for (Course c : courseService.getAll()) {
            println(courseService.getDTO(c).toShortString());
        }
    }

    private static void printTeachers() {
        println("|||  Lecturers |||");
        for (User u : userService.getAllByClass(Teacher.class)) {
            Teacher t = (Teacher) u;
            if (t.isLecturer()) {
                println(userService.getDTO(t).toShortString());
            }
        }
        println("|||  Practice teachers |||");
        for (User u : userService.getAllByClass(Teacher.class)) {
            Teacher t = (Teacher) u;
            if (t.isPractice()) {
                println(userService.getDTO(t).toShortString());
            }
        }
        println("|||  Both |||");
        for (User u : userService.getAllByClass(Teacher.class)) {
            Teacher t = (Teacher) u;
            if (t.isLecturer() && t.isPractice()) {
                println(userService.getDTO(t).toShortString());
            }
        }
    }

    private static void deleteCourse() {
        printAllCourses();
        int id = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        courseService.delete(id);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }
}
