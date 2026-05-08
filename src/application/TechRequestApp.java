package application;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.TechRequest;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import model.enumeration.TechRequestStatus;
import model.enumeration.UIMessage;
import services.TechRequestService;
import services.UserService;
import utils.UIForms;

public final class TechRequestApp extends BaseApp {

    private static final TechRequestService techRequestService = services.techRequestService;
    private static final UserService userService = services.userService;

    private TechRequestApp() {
    }

    public static void startApp() {
        User activeUser = getActiveUser();

        if (activeUser instanceof TechSupportSpecialist) {
            startSpecialistMenu((TechSupportSpecialist) activeUser);
        }
        else{
            startEmployeeMenu(activeUser);
        }
    }

    private static void startEmployeeMenu(User activeUser) {
        ActionMenu menu = new ActionMenu("Technical Requests");
        menu.addAction("Request technical support", () -> handleExceptions(() -> requestTechnicalSupport(activeUser.getId())));
        menu.addAction("View my requests", () -> handleExceptions(() -> viewMyRequests(activeUser.getId())));
        menu.addAction("Exit", menu::stop);
        menu.start();
    }

    private static void startSpecialistMenu(TechSupportSpecialist specialist) {
        ActionMenu menu = new ActionMenu("Technical Requests");
        menu.addAction("View requests by status", () -> handleExceptions(() -> viewRequestsByStatus(specialist.getId())));
        menu.addAction("Update request status", () -> handleExceptions(() -> updateRequestStatus(specialist.getId())));
        menu.addAction("Exit", menu::stop);
        menu.start();
    }

    private static void requestTechnicalSupport(int senderId) {
        printTechSupportSpecialists();
        int specialistId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

        TechRequest request = new TechRequest(senderId, specialistId, content);
        TechRequest createdRequest = techRequestService.sendRequest(request);

        printSuccess("Technical request created.");
        println(techRequestService.getDTO(createdRequest));
    }

    private static void viewMyRequests(int senderId) {
        List<TechRequest> requests = techRequestService.getAllBySenderId(senderId);
        if (requests.isEmpty()) {
            printFail("No technical requests found.");
            return;
        }

        requests.forEach(request -> println(techRequestService.getDTO(request)));
    }

    private static void viewRequestsByStatus(int specialistId) {
        TechRequestStatus selectedStatus = UIForms.readTechRequestStatus(scanner);
        List<TechRequest> requests = techRequestService.getAllBySpecialistId(specialistId).stream()
                .filter(request -> request.getStatus() == selectedStatus)
                .toList();

        if (requests.isEmpty()) {
            printFail("No requests found with status: " + selectedStatus);
            return;
        }

        requests.forEach(request -> println(techRequestService.getDTO(request)));
    }

    private static void updateRequestStatus(int specialistId) {
        List<TechRequest> requests = techRequestService.getAllBySpecialistId(specialistId);
        if (requests.isEmpty()) {
            printFail("No assigned requests.");
            return;
        }

        requests.forEach(request -> println(techRequestService.getDTO(request).toShortString()));

        int requestId = UIForms.readInt(scanner, UIMessage.INPUT_REQUEST_ID);
        TechRequest request = techRequestService.get(requestId);

        if (request.getReceiverId() != specialistId) {
            throw new OperationNotAllowed("You can update only requests addressed to you.");
        }

        TechRequestStatus newStatus = UIForms.readTechRequestStatus(scanner);
        request.setStatus(newStatus);
        techRequestService.updateRequest(request);

        printSuccess("Request status updated.");
        println(techRequestService.getDTO(request));
    }

    private static void printTechSupportSpecialists() {
        List<User> specialists = userService.getAllByClass(TechSupportSpecialist.class);
        if (specialists.isEmpty()) {
            printFail("No technical support specialists found.");
            return;
        }

        println("Tech support specialists:");
        specialists.forEach(user -> println(userService.getDTO(user).toShortString()));
    }
}
