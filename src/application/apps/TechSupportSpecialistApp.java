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
        return new MenuBuilder(UIText.TECH_MENU_TITLE)
                .addAction(UIText.TECH_VIEW_ALL_REQUESTS, () -> printSpecialistRequests())
                .addAction(UIText.TECH_VIEW_BY_STATUS, () -> printSpecialistRequestsByStatus())
                .addAction(UIText.TECH_UPDATE_REQUEST, () -> updateTechRequest())
                .addExit();
    }

    private static void printSpecialistRequests() {
        TechSupportSpecialist specialist = (TechSupportSpecialist) getActiveUser();
        List<TechRequest> requests = techRequestService.getTechRequestsBySpecialist(specialist);
        if (requests.isEmpty()) {
            printFail(UIText.MSG_NO_REQUESTS);
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
            printFail(UIText.MSG_NO_REQUESTS_STATUS, status);
            return;
        }
        printHeader(UIText.TECH_HEADER_STATUS_REQUESTS.localized(status));
        requests.forEach(r -> println(r.asLine()));
    }

    private static void updateTechRequest() {
        TechSupportSpecialist specialist = (TechSupportSpecialist) getActiveUser();
        List<TechRequest> requests = techRequestService.getTechRequestsBySpecialist(specialist);
        if (requests.isEmpty()) {
            printFail(UIText.MSG_NO_TECH_REQUESTS);
            return;
        }
        printHeader(UIText.TECH_HEADER_MY_REQUESTS);
        requests.forEach(r -> println(r.asLine()));
        TechRequest request = UIForms.readIdFromList(scanner, UIText.INPUT_REQUEST_ID, requests);
        TechRequestStatus status = UIForms.readTechRequestStatus(scanner);
        techRequestService.updateStatus(request, status);
        printSuccess(UIText.MSG_REQUEST_UPDATED);
    }
}
