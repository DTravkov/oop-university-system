package application;

import java.util.List;
import java.util.Map;

import exceptions.ApplicationException;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.*;
import model.dto.MessageDTO;
import model.dto.UserDTO;
import model.enumeration.UIMessage;
import services.MessageService;
import services.UserService;
import settings.AppSettings;
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
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 3);

            try {
                switch (choice) {
                    case "1":
                        viewChats();
                        break;
                    case "2":
                        startNewChat();
                        break;
                    case "3":
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
        println("\n|||  " + Translator.translate(UIMessage.MENU_TITLE_MSG) + " |||");
        println("1. View my chats");
        println("2. Start new chat");
        println("3. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void startNewChat() {
        User activeUser = AppSettings.getActiveUser();
        printEmployees();
        int receiverId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

        userService.get(receiverId);
        if (activeUser.getId() == receiverId) {
            throw new OperationNotAllowed("you cannot start a chat with yourself");
        }

        Message message = new Message(activeUser.getId(), receiverId, content);
        messageService.sendMessage(message);

        println(Translator.translate(UIMessage.MSG_SENT));
        Message saved = messageService.get(message.getId());
        println(messageService.getMessageDTO(saved));
    }

    private static void viewChats() {
        while (true) {
            User activeUser = AppSettings.getActiveUser();
            Map<Integer, List<MessageDTO>> chats = messageService.getChatsDTOs(activeUser.getId());

            if (chats.isEmpty()) {
                throw new DoesNotExist("chats for user id=" + activeUser.getId());
            }

            for (var entry : chats.entrySet()) {
                List<MessageDTO> chatMessages = entry.getValue();
                UserDTO chatUser = chatMessages.get(0).getOtherUser(activeUser.getId());
                println(entry.getKey() + ". " + chatUser.getName() + " " + chatUser.getSurname()
                        + " | Message count: " + chatMessages.size());
            }

            int chatChoice = UIForms.readInt(scanner, UIMessage.INPUT_CHAT_ID);
            println("");
            if (!chats.containsKey(chatChoice)) {
                throw new DoesNotExist("chat with id=" + chatChoice);
            }

            List<MessageDTO> selectedChat = chats.get(chatChoice);
            selectedChat.forEach(messageDTO -> println(messageDTO.toShortString()));

            println("1. Send new message");
            println("2. Exit chat");
            String option = UIForms.readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 2);
            switch (option) {
                case "1":
                    String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
                    UserDTO receiver = selectedChat.get(0).getOtherUser(activeUser.getId());
                    messageService.sendMessage(new Message(activeUser.getId(), receiver.getId(), content));
                    break;
                case "2":
                    return;
                default:
                    return;
            }
        }
    }

    private static void printEmployees() {
        println("|||  Employees |||");
        for (User user : userService.getAllByClassOrSubclass(Employee.class)) {
            if (user.getId() != AppSettings.getActiveUser().getId()) {
                println(userService.getDTO(user).toShortString());
            }
        }
    }
}
