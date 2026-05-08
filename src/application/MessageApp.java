package application;

import java.util.List;
import java.util.Map;

import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.Employee;
import model.domain.Message;
import model.domain.User;
import model.dto.MessageDTO;
import model.dto.UserDTO;
import model.enumeration.UIMessage;
import services.MessageService;
import services.UserService;
import settings.AppSettings;
import utils.UIForms;

public final class MessageApp extends BaseApp {

    private static final MessageService messageService = services.messageService;
    private static final UserService userService = services.userService;

    private MessageApp() {
    }

    public static void startApp() {
        ActionMenu menu = new ActionMenu("Messenger");
        menu.addAction("View my chats", () -> handleExceptions(MessageApp::viewChats));
        menu.addAction("Start new chat", () -> handleExceptions(MessageApp::startNewChat));
        menu.addAction("Exit from Messenger", menu::stop);
        menu.start();
    }

    private static void startNewChat() {
        User activeUser = getActiveUser();
        printEmployees();

        int receiverId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
        if (activeUser.getId() == receiverId) {
            throw new OperationNotAllowed("you cannot start a chat with yourself");
        }

        userService.get(receiverId);
        sendMessageTo(receiverId);
    }

    private static void viewChats() {
        User activeUser = getActiveUser();
        Map<Integer, List<MessageDTO>> chats = messageService.getChatsDTOs(activeUser.getId());

        if (chats.isEmpty()) {
            throw new DoesNotExist("chats for user id=" + activeUser.getId());
        }

        println("\n||| Chats |||");
        for (var entry : chats.entrySet()) {
            List<MessageDTO> chatMessages = entry.getValue();
            UserDTO chatUser = chatMessages.get(0).getOtherUser(activeUser.getId());
            println(entry.getKey() + ". " + chatUser.getName() + " " + chatUser.getSurname()
                    + " | Message count: " + chatMessages.size());
        }

        int chatId = UIForms.readInt(scanner, UIMessage.INPUT_CHAT_ID);
        if (!chats.containsKey(chatId)) {
            throw new DoesNotExist("chat with id=" + chatId);
        }

        List<MessageDTO> selectedChat = chats.get(chatId);
        selectedChat.forEach(messageDTO -> println(messageDTO.toShortString()));

        ActionMenu chatMenu = new ActionMenu("Chat actions");
        chatMenu.addAction("Send new message", () -> handleExceptions(() -> sendReply(selectedChat)));
        chatMenu.addAction("Back", chatMenu::stop);
        chatMenu.start();
    }

    private static void sendReply(List<MessageDTO> selectedChat) {
        User activeUser = getActiveUser();
        UserDTO receiver = selectedChat.get(0).getOtherUser(activeUser.getId());
        sendMessageTo(receiver.getId());
    }

    private static void sendMessageTo(int receiverId) {
        User activeUser = getActiveUser();
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
        Message message = new Message(activeUser.getId(), receiverId, content);
        messageService.sendMessage(message);
        printSuccess("Message sent.");
    }

    private static void printEmployees() {
        println("||| Employees |||");
        for (User user : userService.getAllByClassOrSubclass(Employee.class)) {
            if (user.getId() != AppSettings.getActiveUser().getId()) {
                println(userService.getDTO(user).toShortString());
            }
        }
    }
}
