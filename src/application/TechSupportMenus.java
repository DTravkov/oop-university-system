package application;

import java.util.List;

import model.domain.TechRequest;
import model.domain.TechSupportSpecialist;
import model.enumeration.TechRequestStatus;
import model.enumeration.UIMessage;
import services.TechRequestService;
import utils.UIForms;

public class TechSupportMenus extends BaseApp {

    static final TechRequestService techRequestService = services.techRequestService;


    static MenuBuilder getTechSupportSpecMenu() {
        MenuBuilder menu = new MenuBuilder("Technical Specialist Menu");
        menu.addAction("View all requests", () -> printSpecialistRequests());
        menu.addAction("View requests (by status)", () -> printSpecialistRequestsByStatus());
        menu.addAction("Update request", () -> updateTechRequest());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void printSpecialistRequests() {
        TechSupportSpecialist specialist = (TechSupportSpecialist) getActiveUser();
        List<TechRequest> requests = techRequestService.getTechRequestsBySpecialist(specialist);
        if (requests.isEmpty()) {
            printFail("No requests");
            return;
        }
        requests.forEach(r -> println(r.asLine()));
    }

    private static void printSpecialistRequestsByStatus() {
        TechSupportSpecialist specialist = (TechSupportSpecialist) getActiveUser();
        TechRequestStatus status = UIForms.readTechRequestStatus(scanner);
        List<TechRequest> requests = techRequestService.getTechRequestsBySpecialist(specialist).stream()
                .filter(r -> r.getStatus() == status)
                .toList();
        if (requests.isEmpty()) {
            printFail("No requests with status " + status + ".");
            return;
        }
        printHeader(status + " Requests");
        requests.forEach(r -> println(r.asLine()));
    }

    private static void updateTechRequest() {
        TechSupportSpecialist specialist = (TechSupportSpecialist) getActiveUser();
        List<TechRequest> requests = techRequestService.getTechRequestsBySpecialist(specialist);
        if (requests.isEmpty()) {
            printFail("You have no tech requests.");
            return;
        }
        printHeader("My Requests");
        requests.forEach(r -> println(r.asLine()));
        TechRequest request = UIForms.readIdFromList(scanner, UIMessage.INPUT_REQUEST_ID, requests);
        TechRequestStatus status = UIForms.readTechRequestStatus(scanner);
        request.setStatus(status);
        techRequestService.update(request);
        printSuccess("Request updated.");
    }
}
