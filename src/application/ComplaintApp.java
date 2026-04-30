package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import exceptions.OperationNotAllowed;
import model.domain.*;
import model.enumeration.ComplaintUrgencyLevel;
import model.enumeration.UIMessage;
import model.factories.ServiceFactory;
import services.*;
import utils.Translator;
import utils.UIForms;

public class ComplaintApp {

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final ComplaintService complaintService = serviceFactory.getService(ComplaintService.class);
    private static final UserService userService = serviceFactory.getService(UserService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 6);

            try {
                switch (choice) {
                    case "1":
                        sendComplaint(scanner);
                        break;
                    case "2":
                        deleteComplaint(scanner);
                        break;
                    case "3":
                        getAllComplaintsByTeacher(scanner);
                        break;
                    case "4":
                        getAllComplaintsByDean(scanner);
                        break;
                    case "5":
                        getAllComplaints();
                        break;
                    case "6":
                        return;
                    default:
                        System.out.println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Complaints ---");
        System.out.println("1. Send complaint");
        System.out.println("2. Delete complaint by id");
        System.out.println("3. List complaints by teacher id");
        System.out.println("4. List complaints by dean id");
        System.out.println("5. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        System.out.println("6. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void sendComplaint(Scanner scanner) {
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

        switch (urgencyChoice){
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

        TeacherComplaint complaint = new TeacherComplaint(urgencyLevel,teacherId, deanId, studentId, content);
        complaintService.sendComplaint(complaint);

        System.out.println(Translator.translate(UIMessage.MSG_SENT));
        System.out.println("Created: " + complaint);
        System.out.println("Dean addressed complaints: " + complaintService.getAllByDeanId(deanId));
    }

    private static void deleteComplaint(Scanner scanner) {
        getAllComplaints();
        int complaintId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        complaintService.delete(complaintId);

        System.out.println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void getAllComplaintsByTeacher(Scanner scanner) {
        printTeachers();
        int teacherId = UIForms.readInt(scanner, UIMessage.INPUT_SENDER_ID);
        System.out.println(complaintService.getAllByTeacherId(teacherId));
    }

    private static void getAllComplaintsByDean(Scanner scanner) {
        printDeans();
        int deanId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        System.out.println(complaintService.getAllByDeanId(deanId));
    }

    private static void getAllComplaints() {
        System.out.println(complaintService.getAll());
    }
    
    private static void printTeachers() {
        System.out.println("--- Teachers ---");
        for (User user : userService.getAllByClass(Teacher.class)) {
            System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Surname: " + user.getSurname());
        }
    }

    private static void printDeans() {
        System.out.println("--- Deans ---");
        for (User user : userService.getAllByClass(Dean.class)) {
            System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Surname: " + user.getSurname());
        }
    }

    private static void printStudents() {
        System.out.println("--- Students ---");
        for (User user : userService.getAllByClass(Student.class)) {
            System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Surname: " + user.getSurname());
        }
    }


}
