package services;

import java.util.ArrayList;
import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Message;
import model.domain.users.DeletedUser;
import model.domain.users.User;
import model.repository.MessageRepository;
import services.events.Event;
import services.events.UserDeletedEvent;

public class MessageService extends BaseService<Message, MessageRepository>{

    private final UserService userService;

    public MessageService() {
        super(MessageRepository.getInstance());
        this.userService = new UserService();
    }

    public void sendMessage(Message message) {
        User sender = userService.get(message.getSenderId());
        User receiver = userService.get(message.getReceiverId());
        if(sender.getId() == DeletedUser.ID || receiver.getId() == DeletedUser.ID){
            throw new OperationNotAllowed(" sending messages to/from deleted account");
        }
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

    @Override
    public void update(Event e) {
        if(e instanceof UserDeletedEvent event){
            for(Message m : repository.findAllBySenderId(event.getUserId())){
                m.setSenderId(DeletedUser.ID);
                repository.save(m);
                
            }
        }
    }
}
