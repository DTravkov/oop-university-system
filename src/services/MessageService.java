package services;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.DeletedUser;
import model.domain.Message;
import model.domain.User;
import model.factories.ServiceFactory;
import model.repository.MessageRepository;
import services.events.UserDeleteEvent;

public class MessageService extends BaseService<Message, MessageRepository>{

    private final UserService userService;

    public MessageService() {
        super(MessageRepository.getInstance());
        this.userService = ServiceFactory.getInstance().getService(UserService.class);
        subscribeToEvents();
    }

    public void sendMessage(Message message) {
        User sender = userService.get(message.getSenderId());
        User receiver = userService.get(message.getReceiverId());

        if(sender.getId() == DeletedUser.ID || receiver.getId() == DeletedUser.ID){
            throw new OperationNotAllowed(" sending messages to/from deleted account");
        }

        repository.save(message);
    }

    public List<Message> getAllByReceiverId(int receiverId) {
        return repository.findAllByReceiverId(receiverId);
    }

    public List<Message> getAllBySenderId(int senderId) {
        return repository.findAllBySenderId(senderId);
    }

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            this.getAllBySenderId(event.getUserId()).forEach(msg -> {
                msg.setSenderId(DeletedUser.ID);
                repository.save(msg);
            });
        });
    }
}
