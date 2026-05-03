package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import exceptions.OperationNotAllowed;
import model.domain.*;
import model.enumeration.TechRequestStatus;
import model.enumeration.UIMessage;
import model.factories.ServiceFactory;
import services.*;
import utils.Translator;
import utils.UIForms;

public class TechSupportApp {

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final TechRequestService techRequestService = serviceFactory.getService(TechRequestService.class);
    private static final UserService userService = serviceFactory.getService(UserService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 6);

            try {
                switch (choice) {
                    case "1":
                        sendTechRequest(scanner);
                        break;
                    case "2":
                        deleteTechRequest(scanner);
                        break;
                    case "3":
                        getAllTechRequestsBySpecialist(scanner);
                        break;
                    case "4":
                        getAllTechRequestsByStatus(scanner);
                        break;
                    case "5":
                        getAllTechRequests();
                        break;
                    case "6":
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
        System.out.println("\n--- Tech support ---");
        System.out.println("1. Send technical request");
        System.out.println("2. Delete technical request by id");
        System.out.println("3. List technical requests by specialist id");
        System.out.println("4. List technical requests by status");
        System.out.println("5. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        System.out.println("6. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void sendTechRequest(Scanner scanner) {
        printEmployees();
        printTechSupportSpecialists();

        int senderId = UIForms.readInt(scanner, UIMessage.INPUT_SENDER_ID);
        int receiverId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

        userService.get(senderId);
        userService.get(receiverId);

        TechRequest request = new TechRequest(senderId, receiverId, content);
        techRequestService.sendRequest(request);

        System.out.println(Translator.translate(UIMessage.MSG_SENT));
        System.out.println("Created: " + request);
        System.out.println("Specialist queue: " + techRequestService.getAllBySpecialistId(receiverId));
    }

    private static void deleteTechRequest(Scanner scanner) {
        getAllTechRequests();
        int requestId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        techRequestService.delete(requestId);

        System.out.println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void getAllTechRequestsBySpecialist(Scanner scanner) {
        printTechSupportSpecialists();
        int specialistId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        System.out.println(techRequestService.getAllBySpecialistId(specialistId));
    }

    private static void getAllTechRequestsByStatus(Scanner scanner) {
        System.out.println("1. PENDING");
        System.out.println("2. REJECTED");
        System.out.println("3. ACCEPTED");
        System.out.println("4. DONE");
        String statusChoice = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 4);

        TechRequestStatus status;

        switch (statusChoice) {
            case "1":
                status = TechRequestStatus.PENDING;
                break;
            case "2":
                status = TechRequestStatus.REJECTED;
                break;
            case "3":
                status = TechRequestStatus.ACCEPTED;
                break;
            case "4":
                status = TechRequestStatus.DONE;
                break;
            default:
                throw new OperationNotAllowed(" entering invalid technical request status");
        }

        System.out.println(techRequestService.getAllByStatus(status));
    }

    private static void getAllTechRequests() {
        System.out.println(techRequestService.getAll());
    }

    private static void printEmployees() {
        System.out.println("--- Employees ---");
        for (User user : userService.getAllByClassOrSubclass(Employee.class)) {
            System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Surname: " + user.getSurname());
        }
    }

    private static void printTechSupportSpecialists() {
        System.out.println("--- Tech support specialists ---");
        for (User user : userService.getAllByClass(TechSupportSpecialist.class)) {
            System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Surname: " + user.getSurname());
        }
    }

}
