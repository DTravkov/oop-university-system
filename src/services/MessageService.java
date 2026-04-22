package services;

import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import java.util.ArrayList;
import java.util.List;
import model.domain.Message;
import model.domain.User;
import model.repository.MessageRepository;

public class MessageService extends BaseService<Message, MessageRepository> {

    private final UserService userService;

    public MessageService() {
        super(MessageRepository.getInstance());
        this.userService = new UserService();
    }

    public void sendMessage(Message message) {
        User sender = userService.get(message.getSenderId());
        User receiver = userService.get(message.getReceiverId());
        message.setSenderId(sender.getId());
        message.setReceiverId(receiver.getId());
        repository.save(message);
    }

    public List<Message> getAllByReceiverId(int receiverId) {
        User receiver = userService.get(receiverId);
        List<Message> messages = new ArrayList<>(repository.findAllByReceiverId(receiver.getId()));
        return messages;
    }

    public List<Message> getAllBySenderId(int senderId) {
        User sender = userService.get(senderId);
        List<Message> messages = new ArrayList<>(repository.findAllBySenderId(sender.getId()));
        return messages;
    }
}
