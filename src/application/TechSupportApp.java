package application;

import exceptions.ApplicationException;
import exceptions.OperationNotAllowed;
import model.domain.*;
import model.enumeration.TechRequestStatus;
import model.enumeration.UIMessage;
import services.TechRequestService;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public final class TechSupportApp extends BaseApp {

    private static final TechRequestService techRequestService = services.techRequestService;
    private static final UserService userService = services.userService;

    private TechSupportApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 7);

            try {
                switch (choice) {
                    case "1":
                        sendTechRequest();
                        break;
                    case "2":
                        deleteTechRequest();
                        break;
                    case "3":
                        printAllTechRequestsBySpecialist();
                        break;
                    case "4":
                        printAllTechRequestsByStatus();
                        break;
                    case "5":
                        printAllTechRequests();
                        break;
                    case "6":
                        updateTechRequestStage();
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
        println("\n|||  Tech support panel |||");
        println("1. Send technical request");
        println("2. Delete technical request by id");
        println("3. List technical requests by specialist id");
        println("4. List technical requests by status");
        println("5. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        println("6. " + "Update status of a request");
        println("7. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void sendTechRequest() {
        printEmployees();
        printTechSupportSpecialists();

        int senderId = UIForms.readInt(scanner, UIMessage.INPUT_SENDER_ID);
        int receiverId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

        userService.get(senderId);
        userService.get(receiverId);

        TechRequest request = new TechRequest(senderId, receiverId, content);
        techRequestService.sendRequest(request);

        println(Translator.translate(UIMessage.MSG_SENT));
        println(techRequestService.getDTO(request));
        println("Specialist queue:");
        for (TechRequest r : techRequestService.getAllBySpecialistId(receiverId)) {
            println(techRequestService.getDTO(r));
        }
    }

    private static void updateTechRequestStage() {
        for (TechRequest r : techRequestService.getAll()) {
            println(techRequestService.getDTO(r).toShortString());
        }

        int requestId = UIForms.readInt(scanner, UIMessage.INPUT_REQUEST_ID);
        TechRequestStatus newStatus = UIForms.readTechRequestStatus(scanner);

        TechRequest request = techRequestService.get(requestId);
        request.setStatus(newStatus);
        techRequestService.updateRequest(request);

        TechRequest updated = techRequestService.get(requestId);
        println(techRequestService.getDTO(updated));
    }

    private static void deleteTechRequest() {
        printAllTechRequests();
        int requestId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        techRequestService.delete(requestId);

        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void printAllTechRequestsBySpecialist() {
        printTechSupportSpecialists();
        int specialistId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        for (TechRequest r : techRequestService.getAllBySpecialistId(specialistId)) {
            println(techRequestService.getDTO(r).toShortString());
        }
    }

    private static void printAllTechRequestsByStatus() {
        println("1. PENDING");
        println("2. REJECTED");
        println("3. ACCEPTED");
        println("4. DONE");
        String statusChoice = readChoice(UIMessage.MENU_CHOOSE, 1, 4);

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

        for (TechRequest r : techRequestService.getAllByStatus(status)) {
            println(techRequestService.getDTO(r).toShortString());
        }
    }

    private static void printAllTechRequests() {
        for (TechRequest r : techRequestService.getAll()) {
            println(techRequestService.getDTO(r).toShortString());
        }
    }

    private static void printEmployees() {
        println("|||  Employees |||");
        for (User user : userService.getAllByClassOrSubclass(Employee.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }

    private static void printTechSupportSpecialists() {
        println("|||  Tech support specialists |||");
        for (User user : userService.getAllByClass(TechSupportSpecialist.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }
}
