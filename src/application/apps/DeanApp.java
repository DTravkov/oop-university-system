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
        return new MenuBuilder("Complaint Menu")
                .addAction("View pending complaints", () -> printDeanComplaints())
                .addAction("Close pending complaint", () -> closeComplaint())
                .addExit();
    }

    private static void closeComplaint() {
        Dean dean = (Dean) getActiveUser();
        List<TeacherComplaint> pending = getPendingComplaints();
        if (pending.isEmpty()) {
            println("No pending complaints.");
            return;
        }
        printHeader("Complaints");
        pending.forEach(tc -> println(tc.asLine()));
        TeacherComplaint complaint = UIForms.readIdFromList(scanner, UIText.INPUT_REQUEST_ID, pending);
        complaintService.closeComplaint(complaint, dean);
        printSuccess("Complaint closed.");
    }

    private static void printDeanComplaints() {
        Dean activeUser = (Dean) getActiveUser();
        List<TeacherComplaint> pending = getPendingComplaints();
        if (pending.isEmpty()) {
            printFail("You have no complaints yet,");
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
