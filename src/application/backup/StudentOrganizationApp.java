package application;

import exceptions.ApplicationException;
import exceptions.OperationNotAllowed;
import model.domain.Student;
import model.domain.StudentOrganization;
import model.domain.User;
import model.enumeration.UIMessage;
import services.StudentOrganizationService;
import services.UserService;
import settings.AppSettings;
import utils.Translator;
import utils.UIForms;

public final class StudentOrganizationApp extends BaseApp {

    private static final StudentOrganizationService studentOrganizationService =
            services.studentOrganizationService;
    private static final UserService userService = services.userService;

    private StudentOrganizationApp() {
    }

    public static void startApp() {
        User activeUser = AppSettings.getActiveUser();
        boolean isStudent = activeUser instanceof Student;
        while (true) {
            printMenu(isStudent);
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, isStudent ? 6 : 3);

            try {
                if (isStudent) {
                    switch (choice) {
                        case "1":
                            printOrganizations();
                            break;
                        case "2":
                            printOrganizationById();
                            break;
                        case "3":
                            createMyOrganization();
                            break;
                        case "4":
                            deleteMyOrganization(activeUser.getId());
                            break;
                        case "5":
                            manageMyOrganization(activeUser.getId());
                            break;
                        case "6":
                            return;
                        default:
                            printInvalidChoice();
                    }
                } else {
                    switch (choice) {
                        case "1":
                            printOrganizations();
                            break;
                        case "2":
                            printOrganizationById();
                            break;
                        case "3":
                            return;
                        default:
                            printInvalidChoice();
                    }
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    private static void printMenu(boolean isStudent) {
        println("\n|||  " + Translator.translate(UIMessage.MENU_TITLE_ORG) + " |||");
        println("1. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        println("2. " + Translator.translate(UIMessage.ORG_GET_BY_ID));
        if (isStudent) {
            println("3. " + Translator.translate(UIMessage.ORG_CREATE));
            println("4. " + Translator.translate(UIMessage.ORG_DELETE));
            println("5. Manage my organization");
            println("6. " + Translator.translate(UIMessage.MENU_EXIT));
            return;
        }
        println("3. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void createMyOrganization() {
        String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NAME);
        String description = UIForms.readNonEmpty(scanner, UIMessage.INPUT_COURSE_DESC);
        int presidentId = AppSettings.getActiveUser().getId();

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

    private static void deleteMyOrganization(int activeUserId) {
        StudentOrganization organization = getMyOrganizationOrThrow(activeUserId);
        studentOrganizationService.delete(organization.getId());
        println(Translator.translate(UIMessage.MSG_DELETED));
        printOrganizations();
    }

    private static void addMemberToOrganization(int organizationId) {
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        studentOrganizationService.addMember(organizationId, studentId);
        println(studentOrganizationService.getDTO(organizationId));
    }

    private static void deleteMemberFromOrganization(int organizationId) {
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        studentOrganizationService.removeMember(organizationId, studentId);
        println(studentOrganizationService.getDTO(organizationId));
    }

    private static void setOrganizationPresident(int organizationId) {
        printStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        studentOrganizationService.setPresident(organizationId, studentId);
        println(studentOrganizationService.getDTO(organizationId));
    }

    private static void removeOrganizationPresident(int organizationId) {
        studentOrganizationService.removePresident(organizationId);
        println(studentOrganizationService.getDTO(organizationId));
    }

    private static void manageMyOrganization(int activeUserId) {
        StudentOrganization myOrganization = getMyOrganizationOrThrow(activeUserId);
        while (true) {
            printManageMyOrganizationMenu(myOrganization.getId());
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 5);
            switch (choice) {
                case "1":
                    addMemberToOrganization(myOrganization.getId());
                    break;
                case "2":
                    deleteMemberFromOrganization(myOrganization.getId());
                    break;
                case "3":
                    setOrganizationPresident(myOrganization.getId());
                    return;
                case "4":
                    removeOrganizationPresident(myOrganization.getId());
                    return;
                case "5":
                    return;
                default:
                    printInvalidChoice();
            }
        }
    }

    private static void printManageMyOrganizationMenu(int organizationId) {
        println("\n|||  Manage My Organization |||");
        println(studentOrganizationService.getDTO(organizationId).toShortString());
        println("1. " + Translator.translate(UIMessage.ORG_ADD_MEMBER));
        println("2. " + Translator.translate(UIMessage.ORG_REMOVE_MEMBER));
        println("3. " + Translator.translate(UIMessage.ORG_SET_PRESIDENT));
        println("4. " + Translator.translate(UIMessage.ORG_REMOVE_PRESIDENT));
        println("5. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static StudentOrganization getMyOrganizationOrThrow(int activeUserId) {
        StudentOrganization organization = studentOrganizationService.getByPresidentId(activeUserId);
        if (organization == null) {
            throw new OperationNotAllowed("managing organizations where you are not a president");
        }
        return organization;
    }

    private static void printStudents() {
        println(Translator.translate(UIMessage.MSG_STUDENTS_HEADER));
        for (User user : userService.getAllByClass(Student.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }
}
