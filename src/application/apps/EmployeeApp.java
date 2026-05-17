package application.apps;

import java.util.List;

import model.domain.Chat;
import model.domain.Employee;
import model.domain.Message;
import model.domain.TechRequest;
import model.domain.TechSupportSpecialist;
import services.MessageService;
import services.TechRequestService;
import services.UserService;
import utils.UIForms;
import utils.UIText;

public final class EmployeeApp extends BaseApp {

    static final UserService userService = services.userService;
    static final MessageService messageService = services.messageService;
    static final TechRequestService techRequestService = services.techRequestService;

    private EmployeeApp() {
    }

    public static MenuBuilder getMessengerMenu() {
        Employee employee = (Employee) getActiveUser();
        MenuBuilder menu = new MenuBuilder("Messenger");
        menu.addAction("Start new chat", () -> startChat());
        for (Chat chat : messageService.getChatsByMember(employee)) {
            menu.addAction(chat.getTitleFor(employee), () -> openChat(chat));
        }
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    public static MenuBuilder getTechRequestMenu() {
        MenuBuilder menu = new MenuBuilder("Technical Request Menu");
        menu.addAction("View my requests", () -> printTechRequests());
        menu.addAction("Send a new request", () -> sendTechRequest());
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
        Employee emp = UIForms.readIdFromList(scanner, UIText.INPUT_EMPLOYEE_ID, employees);
        String content = UIForms.readNonEmpty(scanner, UIText.INPUT_MESSAGE_CONTENT);
        messageService.sendMessage(new Message(activeUser, content), emp);
    }

    private static void openChat(Chat chat) {
        Employee employee = (Employee) getActiveUser();
        MenuBuilder menu = new MenuBuilder("");
        chat.getMessages().forEach(msg -> menu.addLabel(msg.asLine()));
        menu.addAction("Send Message", () -> {
            String content = UIForms.readNonEmpty(scanner, UIText.INPUT_MESSAGE_CONTENT);
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
                .filter(u -> !u.isBanned() && !u.isDeleted())
                .toList();
        if (specialists.isEmpty()) {
            printFail("No tech support specialists available.");
            return;
        }
        printHeader("Tech Support Specialists");
        specialists.forEach(s -> println(s.asLine()));
        TechSupportSpecialist specialist = UIForms.readIdFromList(scanner, UIText.INPUT_RECEIVER_ID, specialists);
        String content = UIForms.readNonEmpty(scanner, UIText.INPUT_MESSAGE_CONTENT);
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
}
