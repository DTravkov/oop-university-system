package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.OperationNotAllowed;
import model.domain.IMessagable;
import model.domain.Message;
import model.domain.User;
import model.dto.MessageDTO;
import model.repository.MessageRepository;
import services.events.UserDeleteEvent;
import settings.AppSettings;

public class MessageService extends BaseService<Message, MessageRepository>{

    private final UserService userService;

    public MessageService(UserService userService) {
        super(MessageRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }

    public void sendMessage(Message message) {
        User sender = userService.get(message.getSenderId());
        User receiver = userService.get(message.getReceiverId());

        if(sender.getId() == AppSettings.DELETED_USER_ID || receiver.getId() == AppSettings.DELETED_USER_ID){
            throw new OperationNotAllowed(" sending messages to/from deleted account");
        }
        
        if (!(sender instanceof IMessagable) || !(receiver instanceof IMessagable)) {
            throw new OperationNotAllowed(" sending messages to/from non-employee account");
        }
        
        this.create(message);
    }


    public Map<Integer, List<MessageDTO>> getChatsDTOs(int userId){
        Map<Integer, List<Message>> chatMap = getUserChats(userId);
        Map<Integer, List<MessageDTO>> dtoMap = new HashMap<>();
        for(var entry : chatMap.entrySet()){
            List<MessageDTO> messageDTOs = new ArrayList<>();
            entry.getValue().forEach(msg -> {
                messageDTOs.add(getMessageDTO(msg));
            });
            dtoMap.put(entry.getKey(), messageDTOs);
        }
        return dtoMap;
    }

    public MessageDTO getMessageDTO(Message msg){
        return new MessageDTO(msg, userService.getDTO(msg.getSenderId()), userService.getDTO(msg.getReceiverId()));
    }

    public Map<Integer, List<Message>> getUserChats(int userId){
        Map<Integer, List<Message>> chatMap = new HashMap<>();
        for(Message msg : this.getAll()){
            if(msg.getReceiverId() == userId || msg.getSenderId() == userId){
                int otherUserId = msg.getReceiverId() == userId ? msg.getSenderId() : msg.getReceiverId();
                chatMap.computeIfAbsent(otherUserId, (k) -> new ArrayList<>()).add(msg);
            }
        }
        return chatMap;
    }



    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            cleanUpUserMessageData(event.getUserId());
        });
    }

    public void cleanUpUserMessageData(int deletedUserId) {
        List<Message> list = this.getAll();
        for (Message msg : list) {
            if (msg.getSenderId() == deletedUserId || msg.getReceiverId() == deletedUserId) {
                this.delete(msg);
            }
        }
    }
}
