package services;

import java.util.Collection;

import exceptions.DoesNotExist;
import model.domain.Message;
import model.repository.MessageRepository;

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
        if (message.getSender() == null || message.getReceiver() == null) {
            throw new DoesNotExist("Message sender or receiver");
        }

        message.setSender(userService.findOrThrow(message.getSender().getId()));
        message.setReceiver(userService.findOrThrow(message.getReceiver().getId()));
        repository.add(message.getReceiver().getId(), message);
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
