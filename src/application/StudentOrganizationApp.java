package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.StudentOrganization;
import model.domain.Student;
import model.domain.User;
import model.enumeration.UIMessages;
import model.factories.ServiceFactory;
import services.LanguageService;
import services.StudentOrganizationService;
import services.UserService;
import utils.UIFields;

public class StudentOrganizationApp {

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final StudentOrganizationService studentOrganizationService = serviceFactory.getService(StudentOrganizationService.class);
    private static final UserService userService = serviceFactory.getService(UserService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.MENU_CHOOSE, 1, 9);

            try {
                switch (choice) {
                    case "1":
                        createOrganization(scanner);
                        break;
                    case "2":
                        getOrganizationById(scanner);
                        break;
                    case "3":
                        printOrganizations();
                        break;
                    case "4":
                        deleteOrganization(scanner);
                        break;
                    case "5":
                        addMemberToOrganization(scanner);
                        break;
                    case "6":
                        deleteMemberFromOrganization(scanner);
                        break;
                    case "7":
                        setOrganizationPresident(scanner);
                        break;
                    case "8":
                        removeOrganizationPresident(scanner);
                        break;
                    case "9":
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
        System.out.println("\n--- " + LanguageService.translate(UIMessages.MENU_TITLE_ORG) + " ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.ORG_CREATE));
        System.out.println("2. " + LanguageService.translate(UIMessages.ORG_GET_BY_ID));
        System.out.println("3. " + LanguageService.translate(UIMessages.MENU_VIEW_ALL));
        System.out.println("4. " + LanguageService.translate(UIMessages.ORG_DELETE));
        System.out.println("5. " + LanguageService.translate(UIMessages.ORG_ADD_MEMBER));
        System.out.println("6. " + LanguageService.translate(UIMessages.ORG_REMOVE_MEMBER));
        System.out.println("7. " + LanguageService.translate(UIMessages.ORG_SET_PRESIDENT));
        System.out.println("8. " + LanguageService.translate(UIMessages.ORG_REMOVE_PRESIDENT));
        System.out.println("9. " + LanguageService.translate(UIMessages.MENU_EXIT));
    }

    private static void createOrganization(Scanner scanner) {
        String name = UIFields.readNonEmpty(scanner, UIMessages.INPUT_NAME);
        String description = UIFields.readNonEmpty(scanner, UIMessages.INPUT_COURSE_DESC);
        printStudents();
        int presidentId = UIFields.readInt(scanner, UIMessages.INPUT_STUDENT_ID);

        StudentOrganization organization = new StudentOrganization(name, description, presidentId);
        studentOrganizationService.create(organization);

        System.out.println(LanguageService.translate(UIMessages.MSG_CREATED));
        System.out.println("Created: " + organization);
        System.out.println(LanguageService.translate(UIMessages.MSG_ALL_ORGANIZATIONS) + " " + studentOrganizationService.getAll());
    }

    private static void getOrganizationById(Scanner scanner) {
        int organizationId = UIFields.readInt(scanner, UIMessages.INPUT_ORG_ID);
        System.out.println(studentOrganizationService.get(organizationId));
    }

    private static void printOrganizations() {
        System.out.println(studentOrganizationService.getAll());
    }

    private static void deleteOrganization(Scanner scanner) {
        int organizationId = UIFields.readInt(scanner, UIMessages.INPUT_ORG_ID);
        studentOrganizationService.delete(organizationId);
        System.out.println(LanguageService.translate(UIMessages.MSG_DELETED));
        System.out.println(studentOrganizationService.getAll());
    }

    private static void addMemberToOrganization(Scanner scanner) {
        printOrganizations();
        int organizationId = UIFields.readInt(scanner, UIMessages.INPUT_ORG_ID);
        printStudents();
        int studentId = UIFields.readInt(scanner, UIMessages.INPUT_STUDENT_ID);
        studentOrganizationService.addMember(organizationId, studentId);
        System.out.println(studentOrganizationService.get(organizationId));
        System.out.println(LanguageService.translate(UIMessages.MSG_MEMBERS_IDS) + " " + studentOrganizationService.get(organizationId).getMembers());
    }

    private static void deleteMemberFromOrganization(Scanner scanner) {
        printOrganizations();
        int organizationId = UIFields.readInt(scanner, UIMessages.INPUT_ORG_ID);
        printStudents();
        int studentId = UIFields.readInt(scanner, UIMessages.INPUT_STUDENT_ID);
        studentOrganizationService.removeMember(organizationId, studentId);
        System.out.println(studentOrganizationService.get(organizationId));
        System.out.println(LanguageService.translate(UIMessages.MSG_MEMBERS_IDS) + " " + studentOrganizationService.get(organizationId).getMembers());
    }

    private static void setOrganizationPresident(Scanner scanner) {
        printOrganizations();
        int organizationId = UIFields.readInt(scanner, UIMessages.INPUT_ORG_ID);
        printStudents();
        int studentId = UIFields.readInt(scanner, UIMessages.INPUT_STUDENT_ID);
        studentOrganizationService.setPresident(organizationId, studentId);
        System.out.println(studentOrganizationService.get(organizationId));
    }

    private static void removeOrganizationPresident(Scanner scanner) {
        printOrganizations();
        int organizationId = UIFields.readInt(scanner, UIMessages.INPUT_ORG_ID);
        studentOrganizationService.removePresident(organizationId);
        System.out.println(studentOrganizationService.get(organizationId));
    }

    private static void printStudents() {
        System.out.println(LanguageService.translate(UIMessages.MSG_STUDENTS_HEADER));
        for (User user : userService.getAllByClass(Student.class)) {
            System.out.println(user);
        }
    }
}
