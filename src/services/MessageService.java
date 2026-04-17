package services;

import java.util.Collection;

import exceptions.DoesNotExist;
import model.Message;
import model.MessageRepository;

public class MessageService {

    protected final MessageRepository repository;
    private final UserService userService;

    public MessageService() {
        this(new MessageRepository(), new UserService());
    }

    public MessageService(MessageRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public void sendMessage(Message message) {
        userService.findOrThrow(message.getSenderId());
        userService.findOrThrow(message.getReceiverId());
        repository.add(message.getReceiverId(), message);
    }

    public void deleteMessage(int receiverId, int messageId) {
        Message message = findOrThrow(receiverId, messageId);
        repository.delete(receiverId, message);
    }

    public Message findOrThrow(int receiverId, int messageId) {
        for (Message message : repository.getAllByOwnerId(receiverId)) {
            if (message.getId() == messageId) {
                return message;
            }
        }
        throw new DoesNotExist("Message with id : " + messageId);
    }

    public Collection<Message> getAll() {
        return repository.getAll();
    }

    public Collection<Message> getAllByReceiverId(int receiverId) {
        userService.findOrThrow(receiverId);
        return repository.getAllByOwnerId(receiverId);
    }
}
