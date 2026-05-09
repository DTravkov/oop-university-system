package services;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.IMessagable;
import model.domain.Message;
import model.domain.User;
import model.repository.MessageRepository;
import services.events.UserDeleteEvent;
import settings.AppSettings;

public class MessageService extends BaseService<Message, MessageRepository> {

    private final UserService userService;

    public MessageService(UserService userService) {
        super(MessageRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }

    public void sendMessage(Message message) {
        User sender = userService.get(message.getSender().getId());
        User receiver = userService.get(message.getReceiver().getId());

        if (sender.getId() == AppSettings.DELETED_USER_ID || receiver.getId() == AppSettings.DELETED_USER_ID) {
            throw new OperationNotAllowed(" sending messages to/from deleted account");
        }

        if (!(sender instanceof IMessagable) || !(receiver instanceof IMessagable)) {
            throw new OperationNotAllowed(" sending messages to/from non-employee account");
        }

        message.setSender(sender);
        message.setReceiver(receiver);

        this.create(message);
    }

    public List<Message> getAllByReceiverId(int receiverId) {
        return repository.findAllByReceiverId(receiverId);
    }

    public List<Message> getAllBySenderId(int senderId) {
        return repository.findAllBySenderId(senderId);
    }

    @Override
    public void subscribeToEvents() {
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            int deletedUserId = event.getUserId();
            User deletedPlaceholder = userService.get(AppSettings.DELETED_USER_ID);
            for (Message msg : this.getAll()) {
                boolean msgChanged = false;
                if (msg.getSender().getId() == deletedUserId) {
                    msg.setSender(deletedPlaceholder);
                    msgChanged = true;
                }
                if (msg.getReceiver().getId() == deletedUserId) {
                    msg.setReceiver(deletedPlaceholder);
                    msgChanged = true;
                }
                if (msgChanged) {
                    this.update(msg);
                }
            }
        });
    }
}
