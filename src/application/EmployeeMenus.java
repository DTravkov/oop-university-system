package application;

import java.util.List;

import model.domain.Chat;
import model.domain.Dean;
import model.domain.Employee;
import model.domain.Message;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TeacherComplaint;
import model.domain.TechRequest;
import model.domain.TechSupportSpecialist;
import model.enumeration.ComplaintUrgencyLevel;
import model.enumeration.UIMessage;
import services.ComplaintService;
import services.MessageService;
import services.TechRequestService;
import services.UserService;
import utils.UIForms;

public class EmployeeMenus extends BaseApp {

    static final UserService userService = services.userService;
    static final ComplaintService complaintService = services.complaintService;
    static final MessageService messageService = services.messageService;
    static final TechRequestService techRequestService = services.techRequestService;


    static MenuBuilder getMessengerMenu() {
        Employee employee = (Employee) getActiveUser();
        MenuBuilder menu = new MenuBuilder("Messenger");
        menu.addAction("Start new chat", () -> startChat());
        for(Chat chat : messageService.getChatsByMember(employee)){
            menu.addAction(chat.getTitleFor(employee), () -> openChat(chat));
        }
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    static MenuBuilder getTechRequestMenu() {
        MenuBuilder menu = new MenuBuilder("Technical Request Menu");
        menu.addAction("View my requests", () -> printTechRequests());
        menu.addAction("Send a new request", () -> sendTechRequest());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    
    static MenuBuilder getComplaintMenu() {
        MenuBuilder menu = new MenuBuilder("Complaint Menu");
        if(getActiveUser() instanceof Teacher){
            menu.addAction("View my complaints", () -> printTeacherComplaints());
            menu.addAction("Send new complaint", () -> sendComplaint());
        }
        if(getActiveUser() instanceof Dean){
            menu.addAction("View pending complaints", () -> printDeanComplaints());
            menu.addAction("Close pending complaint", () -> closeComplaint());
        }
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }


    

    private static void startChat() {
        Employee activeUser = (Employee) getActiveUser();
        printHeader("Employees");
        List<Employee> employees = userService.getUsersByClass(Employee.class)
                                              .stream()
                                              .filter(u -> !u.equals(activeUser))
                                              .toList();
        if (employees.isEmpty()) {
            println("No employees.");
            return;
        }
        employees.forEach(e -> println(e.asLine()));
        Employee emp = UIForms.readIdFromList(scanner, UIMessage.INPUT_EMPLOYEE_ID, employees);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
        messageService.sendMessage(new Message(activeUser, content), emp);
    }

    private static void openChat(Chat chat) {
        Employee employee = (Employee) getActiveUser();
        MenuBuilder menu = new MenuBuilder("");
        chat.getMessages().forEach(msg -> menu.addLabel(msg.asLine()));
        menu.addAction("Send Message", ()->{
            String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
            messageService.sendMessage(new Message(employee, content), chat.getOtherMember(employee));
            openChat(messageService.get(chat));
            menu.stop();
        });
        menu.addAction("Back", () -> menu.stop());
        menu.start();
    }


    private static void sendTechRequest() {
        Employee employee = (Employee) getActiveUser();
        List<TechSupportSpecialist> specialists = userService.getUsersByClass(TechSupportSpecialist.class)
                                                                .stream()
                                                                .filter(u -> !u.isBanned() || u.isDeleted())
                                                                .toList();
        if (specialists.isEmpty()) {
            printFail("No tech support specialists available.");
            return;
        }
        printHeader("Tech Support Specialists");
        specialists.forEach(s -> println(s.asLine()));
        TechSupportSpecialist specialist = UIForms.readIdFromList(scanner, UIMessage.INPUT_RECEIVER_ID, specialists);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
        techRequestService.create(new TechRequest(employee, specialist, content));
        printSuccess("Tech request sent.");
    }

    private static void printTechRequests() {
        Employee employee = (Employee) getActiveUser();
        List<TechRequest> requests = techRequestService.getTechRequestsByEmployee(employee);
        if (requests.isEmpty()) {
            printFail("You have no tech requests yet.");
            return;
        }
        requests.forEach(r -> println(r.asLine()));
    }


    private static void sendComplaint() {
            Teacher teacher = (Teacher) getActiveUser();

            List<Dean> deans = userService.getUsersByClass(Dean.class);
            if (deans.isEmpty()) {
                println("No deans.");
                return;
            }
            printHeader("Deans");
            deans.forEach(d -> println(d.asLine()));
            Dean dean = UIForms.readIdFromList(scanner, UIMessage.INPUT_RECEIVER_ID, deans);

            List<Student> students = userService.getUsersByClass(Student.class);
            if (students.isEmpty()) {
                println("No students.");
                return;
            }
            printHeader("Students");
            students.forEach(s -> println(s.asLine()));
            Student student = UIForms.readIdFromList(scanner, UIMessage.INPUT_STUDENT_ID, students);

            ComplaintUrgencyLevel urgency = UIForms.readComplaintUrgencyLevel(scanner);
            String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

            TeacherComplaint complaint = new TeacherComplaint(urgency, teacher, dean, student, content);
            complaintService.create(complaint);
            printSuccess("Complaint sent.");
    }

    private static void closeComplaint() {
            Dean dean = (Dean) getActiveUser();
            List<TeacherComplaint> pending = complaintService.getComplaintsByDean(dean);
            if (pending.isEmpty()) {
                println("No pending complaints.");
                return;
            }
            printHeader("Complaints");
            pending.forEach(tc -> println(tc.asLine()));
            TeacherComplaint complaint = UIForms.readIdFromList(scanner, UIMessage.INPUT_REQUEST_ID, pending);
            complaintService.closeComplaint(complaint, dean);
            printSuccess("Complaint closed.");
    }

    private static void printTeacherComplaints() {
        Teacher activeUser = (Teacher) getActiveUser();
        List<TeacherComplaint> complaints = complaintService.getComplaintsByTeacher(activeUser);
        if(complaints.isEmpty()){
            printFail("You have no complaints yet,");
            return;
        }
        complaints.forEach(tc -> println(tc.asLine()));
    }

    private static void printDeanComplaints() {
        Dean activeUser = (Dean) getActiveUser();
        List<TeacherComplaint> complaints = complaintService.getComplaintsByDean(activeUser);
        if(complaints.isEmpty()){
            printFail("You have no complaints yet,");
            return;
        }
        complaints.forEach(tc -> println(tc.asLine()));
    }
}
