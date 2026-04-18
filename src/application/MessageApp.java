package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import exceptions.DoesNotExist;
import model.domain.Message;
import model.domain.SessionData;
import model.enumeration.UIMessages;
import model.domain.User;
import services.LanguageService;
import services.MessageService;
import services.UserService;
import utils.UIFields;

public class MessageApp {

    private static final MessageService service = new MessageService();
    private static final UserService userService = new UserService();

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        sendMessage(scanner);
                        break;
                    case "2":
                        getInbox();
                        break;
                    case "3":
                        deleteMessage(scanner);
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(LanguageService.translate(e.getMessageKey(), e.getArgs()));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + LanguageService.translate(UIMessages.TITLE_MSG) + " ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.SEND_MSG));
        System.out.println("2. " + LanguageService.translate(UIMessages.INBOX));
        System.out.println("3. " + LanguageService.translate(UIMessages.DELETE_MSG));
        System.out.println("4. " + LanguageService.translate(UIMessages.EXIT));
        System.out.println("--------------------");
        System.out.print(LanguageService.translate(UIMessages.CHOOSE));
    }

    private static void sendMessage(Scanner scanner) {
        User currentUser = requireCurrentUser();
        int receiverId = UIFields.readInt(scanner, "MESSAGE SEND", UIMessages.RECEIVER_ID);
        String content = UIFields.readNonEmpty(scanner, "MESSAGE SEND", UIMessages.MESSAGE_CONTENT);
        User receiver = userService.findOrThrow(receiverId);

        service.sendMessage(new Message(currentUser, receiver, content));

        System.out.println(LanguageService.translate(UIMessages.SENT));
    }

    private static void getInbox() {
        User currentUser = requireCurrentUser();
        System.out.println(service.getAllByReceiverId(currentUser.getId()));
    }

    private static void deleteMessage(Scanner scanner) {
        User currentUser = requireCurrentUser();
        int messageId = UIFields.readInt(scanner, "MESSAGE DELETE", UIMessages.MESSAGE_ID);

        service.deleteMessage(currentUser.getId(), messageId);

        System.out.println(LanguageService.translate(UIMessages.DELETED));
    }

    private static User requireCurrentUser() {
        User user = SessionData.getInstance().getUser();
        if (user == null) {
            throw new DoesNotExist("Current session user");
        }
        return user;
    }
}
