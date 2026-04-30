package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.StudentOrganization;
import model.domain.Student;
import model.domain.User;
import model.enumeration.UIMessage;
import model.factories.ServiceFactory;
import services.StudentOrganizationService;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public class StudentOrganizationApp {

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final StudentOrganizationService studentOrganizationService = serviceFactory.getService(StudentOrganizationService.class);
    private static final UserService userService = serviceFactory.getService(UserService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 9);

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
                        System.out.println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + Translator.translate(UIMessage.MENU_TITLE_ORG) + " ---");
        System.out.println("1. " + Translator.translate(UIMessage.ORG_CREATE));
        System.out.println("2. " + Translator.translate(UIMessage.ORG_GET_BY_ID));
        System.out.println("3. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        System.out.println("4. " + Translator.translate(UIMessage.ORG_DELETE));
        System.out.println("5. " + Translator.translate(UIMessage.ORG_ADD_MEMBER));
        System.out.println("6. " + Translator.translate(UIMessage.ORG_REMOVE_MEMBER));
        System.out.println("7. " + Translator.translate(UIMessage.ORG_SET_PRESIDENT));
        System.out.println("8. " + Translator.translate(UIMessage.ORG_REMOVE_PRESIDENT));
        System.out.println("9. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void createOrganization(Scanner scanner) {
        String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NAME);
        String description = UIForms.readNonEmpty(scanner, UIMessage.INPUT_COURSE_DESC);
        printStudents();
        int presidentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);

        StudentOrganization organization = new StudentOrganization(name, description, presidentId);
        studentOrganizationService.create(organization);

        System.out.println(Translator.translate(UIMessage.MSG_CREATED));
        System.out.println("Created: " + organization);
        System.out.println(Translator.translate(UIMessage.MSG_ALL_ORGANIZATIONS));
        System.out.println(studentOrganizationService.getAll());
    }

    private static void getOrganizationById(Scanner scanner) {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        System.out.println(studentOrganizationService.get(organizationId));
    }

    private static void printOrganizations() {
        System.out.println(studentOrganizationService.getAll());
    }

    private static void deleteOrganization(Scanner scanner) {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        studentOrganizationService.delete(organizationId);
        System.out.println(Translator.translate(UIMessage.MSG_DELETED));
        System.out.println(studentOrganizationService.getAll());
    }

    private static void addMemberToOrganization(Scanner scanner) {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        studentOrganizationService.addMember(organizationId, studentId);
        System.out.println(studentOrganizationService.get(organizationId));
        System.out.println(Translator.translate(UIMessage.MSG_MEMBERS_IDS));
        System.out.println(studentOrganizationService.get(organizationId).getMembers());
    }

    private static void deleteMemberFromOrganization(Scanner scanner) {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        studentOrganizationService.removeMember(organizationId, studentId);
        System.out.println(studentOrganizationService.get(organizationId));
        System.out.println(Translator.translate(UIMessage.MSG_MEMBERS_IDS));
        System.out.println(studentOrganizationService.get(organizationId).getMembers());
    }

    private static void setOrganizationPresident(Scanner scanner) {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        studentOrganizationService.setPresident(organizationId, studentId);
        System.out.println(studentOrganizationService.get(organizationId));
    }

    private static void removeOrganizationPresident(Scanner scanner) {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        studentOrganizationService.removePresident(organizationId);
        System.out.println(studentOrganizationService.get(organizationId));
    }

    private static void printStudents() {
        System.out.println(Translator.translate(UIMessage.MSG_STUDENTS_HEADER));
        for (User user : userService.getAllByClass(Student.class)) {
            System.out.println(user);
        }
    }
}
