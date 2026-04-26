package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.UIMessages;
import model.factories.ServiceFactory;
import services.*;
import utils.UIFields;

public class MessageApp {

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final MessageService messageService = serviceFactory.getService(MessageService.class);
    private static final UserService userService = serviceFactory.getService(UserService.class);

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.MENU_CHOOSE, 1, 6);

            try {
                switch (choice) {
                    case "1":
                        sendMessage(scanner);
                        break;
                    case "2":
                        deleteMessage(scanner);
                        break;
                    case "3":
                        getMessagesBySender(scanner);
                        break;
                    case "4":
                        getMessagesByReceiver(scanner);
                        break;
                    case "5":
                        getAllMessages();
                        break;
                    case "6":
                        return;
                    default:
                        System.out.println(LanguageService.translate(UIMessages.MSG_INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(LanguageService.translate(e.getMessageKey(), e.getArgs()));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + LanguageService.translate(UIMessages.MENU_TITLE_MSG) + " ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.MSG_SEND));
        System.out.println("2. Delete message by id");
        System.out.println("3. List messages by sender id");
        System.out.println("4. List messages by receiver id");
        System.out.println("5. " + LanguageService.translate(UIMessages.MENU_VIEW_ALL));
        System.out.println("6. " + LanguageService.translate(UIMessages.MENU_EXIT));
    }

    private static void sendMessage(Scanner scanner) {
        printUsers();
        int senderId = UIFields.readInt(scanner, UIMessages.INPUT_SENDER_ID);
        int receiverId = UIFields.readInt(scanner, UIMessages.INPUT_RECEIVER_ID);
        String content = UIFields.readNonEmpty(scanner, UIMessages.INPUT_MESSAGE_CONTENT);

        userService.get(senderId);
        userService.get(receiverId);

        Message message = new Message(senderId, receiverId, content);
        messageService.sendMessage(message);

        System.out.println(LanguageService.translate(UIMessages.MSG_SENT));
        System.out.println("Created: " + message);
        System.out.println("Receiver inbox: " + messageService.getAllByReceiverId(receiverId));
    }

    private static void deleteMessage(Scanner scanner) {
        getAllMessages();
        int messageId = UIFields.readInt(scanner, UIMessages.INPUT_MESSAGE_ID);
        messageService.delete(messageId);

        System.out.println(LanguageService.translate(UIMessages.MSG_DELETED));
    }

    private static void getMessagesBySender(Scanner scanner) {
        printUsers();
        int senderId = UIFields.readInt(scanner, UIMessages.INPUT_SENDER_ID);
        System.out.println(messageService.getAllBySenderId(senderId));
    }

    private static void getMessagesByReceiver(Scanner scanner) {
        printUsers();
        int receiverId = UIFields.readInt(scanner, UIMessages.INPUT_RECEIVER_ID);
        System.out.println(messageService.getAllByReceiverId(receiverId));
    }

    private static void getAllMessages() {
        System.out.println(messageService.getAll());
    }
    
    private static void printUsers() {
        System.out.println("--- Employees ---");
        for (User user : userService.getAllByClassOrSubclass(Employee.class)) {
            System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Surname: " + user.getSurname());
        }
    }
}
