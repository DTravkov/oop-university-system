package application;

import java.util.List;

import exceptions.ApplicationException;
import exceptions.OperationNotAllowed;
import model.domain.Employee;
import model.domain.TechRequest;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import model.enumeration.TechRequestStatus;
import model.enumeration.UIMessage;
import services.TechRequestService;
import services.UserService;
import settings.AppSettings;
import utils.Translator;
import utils.UIForms;

public final class TechRequestApp extends BaseApp {

    private static final TechRequestService techRequestService = services.techRequestService;
    private static final UserService userService = services.userService;

    private TechRequestApp() {
    }

    public static void startApp() {
        User activeUser = AppSettings.getActiveUser();
        if (!(activeUser instanceof Employee)) {
            throw new OperationNotAllowed("technical requests are available only for employees");
        }

        while (true) {
            try {
                if (activeUser instanceof TechSupportSpecialist) {
                    printSpecialistMenu();
                    String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 4);
                    switch (choice) {
                        case "1":
                            printAllRequests();
                            break;
                        case "2":
                            printSpecialistQueue(activeUser.getId());
                            break;
                        case "3":
                            updateRequestStatus();
                            break;
                        case "4":
                            return;
                        default:
                            printInvalidChoice();
                    }
                } else {
                    printEmployeeMenu();
                    String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 3);
                    switch (choice) {
                        case "1":
                            startNewRequest(activeUser.getId());
                            break;
                        case "2":
                            printEmployeeRequests(activeUser.getId());
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

    private static void printEmployeeMenu() {
        println("\n|||  Tech requests |||");
        println("1. Apply a technical request");
        println("2. Check my technical requests");
        println("3. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void printSpecialistMenu() {
        println("\n|||  Tech requests management |||");
        println("1. View all tech requests");
        println("2. View my assigned requests");
        println("3. Update request status");
        println("4. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void startNewRequest(int senderId) {
        printTechSupportSpecialists();
        int receiverId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

        TechRequest request = new TechRequest(senderId, receiverId, content);
        techRequestService.sendRequest(request);

        println(Translator.translate(UIMessage.MSG_SENT));
    }

    private static void printEmployeeRequests(int senderId) {
        List<TechRequest> requests = techRequestService.getAllBySenderId(senderId);
        if (requests.isEmpty()) {
            printFail("No tech requests found");
            return;
        }
        requests.forEach(request -> println(techRequestService.getDTO(request).toShortString()));
    }

    private static void printSpecialistQueue(int specialistId) {
        List<TechRequest> requests = techRequestService.getAllBySpecialistId(specialistId);
        if (requests.isEmpty()) {
            printFail("No requests assigned");
            return;
        }
        requests.forEach(request -> println(techRequestService.getDTO(request).toShortString()));
    }

    private static void printAllRequests() {
        List<TechRequest> requests = techRequestService.getAll();
        if (requests.isEmpty()) {
            printFail("No tech requests found");
            return;
        }
        requests.forEach(request -> println(techRequestService.getDTO(request).toShortString()));
    }

    private static void updateRequestStatus() {
        printAllRequests();
        int requestId = UIForms.readInt(scanner, UIMessage.INPUT_REQUEST_ID);
        TechRequestStatus newStatus = UIForms.readTechRequestStatus(scanner);

        TechRequest request = techRequestService.get(requestId);
        request.setStatus(newStatus);
        techRequestService.updateRequest(request);
        println(techRequestService.getDTO(request));
    }

    private static void printTechSupportSpecialists() {
        println("|||  Tech support specialists |||");
        for (User user : userService.getAllByClass(TechSupportSpecialist.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }
}
