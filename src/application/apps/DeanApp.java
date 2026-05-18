package application.apps;

import java.util.List;

import model.domain.Dean;
import model.domain.TeacherComplaint;
import services.ComplaintService;
import utils.UIForms;
import utils.UIText;

public final class DeanApp extends BaseApp {

    static final ComplaintService complaintService = services.complaintService;

    public DeanApp() {
        super();
    }

    public static MenuBuilder getMenu() {
        return new MenuBuilder(UIText.DEAN_MENU_TITLE)
                .addAction(UIText.DEAN_VIEW_COMPLAINTS, () -> printDeanComplaints())
                .addAction(UIText.DEAN_CLOSE_COMPLAINT, () -> closeComplaint())
                .addExit();
    }

    private static void closeComplaint() {
        Dean dean = (Dean) getActiveUser();
        List<TeacherComplaint> pending = getPendingComplaints();
        if (pending.isEmpty()) {
            println(UIText.MSG_NO_PENDING_COMPLAINTS);
            return;
        }
        printHeader(UIText.DEAN_HEADER_COMPLAINTS);
        pending.forEach(tc -> println(tc.asLine()));
        TeacherComplaint complaint = UIForms.readIdFromList(scanner, UIText.INPUT_REQUEST_ID, pending);
        complaintService.closeComplaint(complaint, dean);
        printSuccess(UIText.MSG_COMPLAINT_CLOSED);
    }

    private static void printDeanComplaints() {
        List<TeacherComplaint> pending = getPendingComplaints();
        if (pending.isEmpty()) {
            printFail(UIText.MSG_NO_COMPLAINTS_YET);
            return;
        }
        pending.forEach(tc -> println(tc.asLine()));
    }

    private static List<TeacherComplaint> getPendingComplaints() {
        Dean activeUser = (Dean) getActiveUser();
        List<TeacherComplaint> complaints = complaintService.getComplaintsByDean(activeUser)
                                                        .stream()
                                                        .filter(c -> !c.isClosed())
                                                        .toList();
        return complaints;
    }
}
