package application;

import exceptions.ApplicationException;
import model.domain.*;
import model.enumeration.UIMessage;
import services.MessageService;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public final class MessageApp extends BaseApp {

    private static final MessageService messageService = services.messageService;
    private static final UserService userService = services.userService;

    private MessageApp() {
    }

    public static void startApp() {
        while (true) {
            printMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 6);

            try {
                switch (choice) {
                    case "1":
                        sendMessage();
                        break;
                    case "2":
                        deleteMessage();
                        break;
                    case "3":
                        printMessagesBySender();
                        break;
                    case "4":
                        printMessagesByReceiver();
                        break;
                    case "5":
                        getAllMessages();
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
        println("\n--- " + Translator.translate(UIMessage.MENU_TITLE_MSG) + " ---");
        println("1. " + Translator.translate(UIMessage.MSG_SEND));
        println("2. Delete message by id");
        println("3. List messages by sender id");
        println("4. List messages by receiver id");
        println("5. " + Translator.translate(UIMessage.MENU_VIEW_ALL));
        println("6. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void sendMessage() {
        printEmployees();
        int senderId = UIForms.readInt(scanner, UIMessage.INPUT_SENDER_ID);
        int receiverId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

        userService.get(senderId);
        userService.get(receiverId);

        Message message = new Message(senderId, receiverId, content);
        messageService.sendMessage(message);

        println(Translator.translate(UIMessage.MSG_SENT));
        Message saved = messageService.get(message.getId());
        println(messageService.getDTO(saved));
        println("Receiver inbox:");
        for (Message m : messageService.getAllByReceiverId(receiverId)) {
            println(messageService.getDTO(m));
        }
    }

    private static void deleteMessage() {
        getAllMessages();
        int messageId = UIForms.readInt(scanner, UIMessage.INPUT_MESSAGE_ID);
        messageService.delete(messageId);

        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void printMessagesBySender() {
        printEmployees();
        int senderId = UIForms.readInt(scanner, UIMessage.INPUT_SENDER_ID);
        println("--- Messages ---");
        for (Message m : messageService.getAllBySenderId(senderId)) {
            println(messageService.getDTO(m).toShortString());
        }
    }

    private static void printMessagesByReceiver() {
        printEmployees();
        int receiverId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        println("--- Messages ---");
        for (Message m : messageService.getAllByReceiverId(receiverId)) {
            println(messageService.getDTO(m).toShortString());
        }
    }

    private static void getAllMessages() {
        println("--- Messages ---");
        for (Message m : messageService.getAll()) {
            println(messageService.getDTO(m).toShortString());
        }
    }

    private static void printEmployees() {
        println("--- Employees ---");
        for (User user : userService.getAllByClassOrSubclass(Employee.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }
}
