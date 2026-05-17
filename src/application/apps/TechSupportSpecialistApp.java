package application.apps;

import java.util.List;

import model.domain.TechRequest;
import model.domain.TechSupportSpecialist;
import model.enumeration.TechRequestStatus;
import services.TechRequestService;
import utils.UIForms;
import utils.UIText;

public final class TechSupportSpecialistApp extends BaseApp {

    static final TechRequestService techRequestService = services.techRequestService;

    public TechSupportSpecialistApp() {
        super();
    }

    public static MenuBuilder getMenu() {
        return new MenuBuilder("Technical Specialist Menu")
                .addAction("View all requests", () -> printSpecialistRequests())
                .addAction("View requests (by status)", () -> printSpecialistRequestsByStatus())
                .addAction("Update request", () -> updateTechRequest())
                .addExit();
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
        TechRequest request = UIForms.readIdFromList(scanner, UIText.INPUT_REQUEST_ID, requests);
        TechRequestStatus status = UIForms.readTechRequestStatus(scanner);
        techRequestService.updateStatus(request, status);
        printSuccess("Request updated.");
    }
}
