package application;

import exceptions.ApplicationException;
import exceptions.OperationNotAllowed;
import model.domain.*;
import model.enumeration.ComplaintUrgencyLevel;
import model.enumeration.UIMessage;
import services.ComplaintService;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public final class ComplaintApp extends BaseApp {

    private static final ComplaintService complaintService = services.complaintService;
    private static final UserService userService = services.userService;

    private ComplaintApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 6);

            try {
                switch (choice) {
                    case "1":
                        sendComplaint();
                        break;
                    case "2":
                        deleteComplaint();
                        break;
                    case "3":
                        printAllComplaintsByTeacher();
                        break;
                    case "4":
                        printAllComplaintsByDean();
                        break;
                    case "5":
                        printAllComplaints();
                        break;
                    case "6":
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
        println("\n--- Complaints ---");
        println("1. Send complaint");
        println("2. Delete complaint by id");
        println("3. List complaints by teacher id");
        println("4. List complaints by dean id");
        println("5. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        println("6. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void sendComplaint() {
        printTeachers();
        printDeans();

        int teacherId = UIForms.readInt(scanner, UIMessage.INPUT_SENDER_ID);
        int deanId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        int urgencyChoice = UIForms.readInt(scanner, UIMessage.INPUT_COMPLAINT_LEVEL);

        printStudents();

        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

        userService.get(teacherId);
        userService.get(deanId);
        userService.get(studentId);

        ComplaintUrgencyLevel urgencyLevel;

        switch (urgencyChoice) {
            case 1:
                urgencyLevel = ComplaintUrgencyLevel.LOW;
                break;
            case 2:
                urgencyLevel = ComplaintUrgencyLevel.AVERAGE;
                break;
            case 3:
                urgencyLevel = ComplaintUrgencyLevel.HIGH;
                break;
            default:
                throw new OperationNotAllowed(" entering invalid urgency level");
        }

        TeacherComplaint complaint = new TeacherComplaint(urgencyLevel, teacherId, deanId, studentId, content);
        complaintService.sendComplaint(complaint);

        println(Translator.translate(UIMessage.MSG_SENT));
        TeacherComplaint saved = complaintService.get(complaint.getId());
        println(complaintService.getDTO(saved));
        println("Dean addressed complaints:");
        for (TeacherComplaint c : complaintService.getAllByDeanId(deanId)) {
            println(complaintService.getDTO(c));
        }
    }

    private static void deleteComplaint() {
        printAllComplaints();
        int complaintId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        complaintService.delete(complaintId);

        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void printAllComplaintsByTeacher() {
        printTeachers();
        int teacherId = UIForms.readInt(scanner, UIMessage.INPUT_SENDER_ID);
        for (TeacherComplaint c : complaintService.getAllByTeacherId(teacherId)) {
            println(complaintService.getDTO(c).toShortString());
        }
    }

    private static void printAllComplaintsByDean() {
        printDeans();
        int deanId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        for (TeacherComplaint c : complaintService.getAllByDeanId(deanId)) {
            println(complaintService.getDTO(c).toShortString());
        }
    }

    private static void printAllComplaints() {
        for (TeacherComplaint c : complaintService.getAll()) {
            println(complaintService.getDTO(c).toShortString());
        }
    }

    private static void printTeachers() {
        println("--- Teachers ---");
        for (User user : userService.getAllByClass(Teacher.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }

    private static void printDeans() {
        println("--- Deans ---");
        for (User user : userService.getAllByClass(Dean.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }

    private static void printStudents() {
        println("--- Students ---");
        for (User user : userService.getAllByClass(Student.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }
}
