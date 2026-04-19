package services;

import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import java.util.List;
import java.util.stream.Collectors;
import model.domain.Message;
import model.domain.User;
import model.repository.MessageRepository;

public class MessageService extends BaseService<Message, MessageRepository> {

    private final UserService userService;

    public MessageService() {
        super(new MessageRepository());
        this.userService = new UserService();
    }

    public void sendMessage(Message message) {
        message.setSender(userService.get(message.getSender().getId()));
        message.setReceiver(userService.get(message.getReceiver().getId()));
        repository.save(message);
    }

    public void deleteMessage(int id) {
        get(id);
        repository.delete(id);
    }

    public void deleteMessage(int userId, int messageId) {
        Message message = get(messageId);
        if (message.getReceiver().getId() != userId && message.getSender().getId() != userId) {
            throw new OperationNotAllowed("Cannot delete another user's message");
        }
        repository.delete(messageId);
    }

    public List<Message> getAllByReceiverId(int receiverId) {
        User receiver = userService.get(receiverId);
        List<Message> messages = getAll().stream()
                .filter(message -> message.getReceiver().getId() == receiver.getId())
                .collect(Collectors.toList());
        if (messages.isEmpty()) {
            throw new DoesNotExist("Messages for receiver id " + receiverId);
        }
        return messages;
    }
}
