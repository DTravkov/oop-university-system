package application;

import exceptions.ApplicationException;
import model.domain.Student;
import model.domain.StudentOrganization;
import model.domain.User;
import model.enumeration.UIMessage;
import services.StudentOrganizationService;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public final class StudentOrganizationApp extends BaseApp {

    private static final StudentOrganizationService studentOrganizationService =
            services.studentOrganizationService;
    private static final UserService userService = services.userService;

    private StudentOrganizationApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 9);

            try {
                switch (choice) {
                    case "1":
                        createOrganization();
                        break;
                    case "2":
                        printOrganizationById();
                        break;
                    case "3":
                        printOrganizations();
                        break;
                    case "4":
                        deleteOrganization();
                        break;
                    case "5":
                        addMemberToOrganization();
                        break;
                    case "6":
                        deleteMemberFromOrganization();
                        break;
                    case "7":
                        setOrganizationPresident();
                        break;
                    case "8":
                        removeOrganizationPresident();
                        break;
                    case "9":
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
        println("\n|||  " + Translator.translate(UIMessage.MENU_TITLE_ORG) + " |||");
        println("1. " + Translator.translate(UIMessage.ORG_CREATE));
        println("2. " + Translator.translate(UIMessage.ORG_GET_BY_ID));
        println("3. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        println("4. " + Translator.translate(UIMessage.ORG_DELETE));
        println("5. " + Translator.translate(UIMessage.ORG_ADD_MEMBER));
        println("6. " + Translator.translate(UIMessage.ORG_REMOVE_MEMBER));
        println("7. " + Translator.translate(UIMessage.ORG_SET_PRESIDENT));
        println("8. " + Translator.translate(UIMessage.ORG_REMOVE_PRESIDENT));
        println("9. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void createOrganization() {
        String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NAME);
        String description = UIForms.readNonEmpty(scanner, UIMessage.INPUT_COURSE_DESC);
        printStudents();
        int presidentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);

        StudentOrganization organization = new StudentOrganization(name, description, presidentId);
        studentOrganizationService.create(organization);

        println(Translator.translate(UIMessage.MSG_CREATED));
        println(studentOrganizationService.getDTO(organization.getId()));
        println(Translator.translate(UIMessage.MSG_ALL_ORGANIZATIONS));
        for (StudentOrganization org : studentOrganizationService.getAll()) {
            println(studentOrganizationService.getDTO(org));
        }
    }

    private static void printOrganizationById() {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        println(studentOrganizationService.getDTO(organizationId));
    }

    private static void printOrganizations() {
        for (StudentOrganization org : studentOrganizationService.getAll()) {
            println(studentOrganizationService.getDTO(org).toShortString());
        }
    }

    private static void deleteOrganization() {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        studentOrganizationService.delete(organizationId);
        println(Translator.translate(UIMessage.MSG_DELETED));
        printOrganizations();
    }

    private static void addMemberToOrganization() {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        studentOrganizationService.addMember(organizationId, studentId);
        println(studentOrganizationService.getDTO(organizationId));
    }

    private static void deleteMemberFromOrganization() {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        studentOrganizationService.removeMember(organizationId, studentId);
        println(studentOrganizationService.getDTO(organizationId));
    }

    private static void setOrganizationPresident() {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        studentOrganizationService.setPresident(organizationId, studentId);
        println(studentOrganizationService.getDTO(organizationId));
    }

    private static void removeOrganizationPresident() {
        printOrganizations();
        int organizationId = UIForms.readInt(scanner, UIMessage.INPUT_ORG_ID);
        studentOrganizationService.removePresident(organizationId);
        println(studentOrganizationService.getDTO(organizationId));
    }

    private static void printStudents() {
        println(Translator.translate(UIMessage.MSG_STUDENTS_HEADER));
        for (User user : userService.getAllByClass(Student.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }
}
