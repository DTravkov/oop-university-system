package services;

import java.util.List;

import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.Chat;
import model.domain.Employee;
import model.domain.Message;
import model.domain.User;
import services.events.UserDeleteEvent;

public class MessageService extends BaseService<Chat>{


    public MessageService() {
        super(Chat.class);
        subscribeToEvents();
    }

    public Chat sendMessage(Chat chat, Message msg) {
        Employee sender = (Employee) msg.getSender();
        if (!chat.isMember(sender)) {
            throw new DoesNotExist(msg.getSender() + " is not a member of the chat");
        }
        chat.sendMessage(msg);
        update(chat);
        return chat;
    }

    public Chat sendMessage(Message msg, Employee to) {

        Employee sender = (Employee) msg.getSender();
        Employee receiver = (Employee) to;

        if(sender.getId() <= 0 || receiver.getId() <= 0){
            throw new OperationNotAllowed(" sending messages to/from deleted accounts");
        }
        
        Chat chat;

        if(exists(sender, receiver)){
            chat = get(sender, receiver);
            chat.sendMessage(msg);
            update(chat);
            return chat;
        }

        chat = this.create(new Chat(sender, receiver));
        chat.sendMessage(msg);
        update(chat);
        return chat;
    }



    public List<Chat> getAllChats(Employee chatMember){
        return getAll().stream().filter(ch -> ch.isMember(chatMember)).toList();
    }

    public Chat get(Employee memberOne, Employee memberTwo){
        return getAll().stream()
                       .filter(chat -> chat.isMember(memberOne) && chat.isMember(memberTwo))
                       .findFirst()
                       .orElseThrow(() -> new DoesNotExist("chat for users " + memberOne + " and " + memberTwo));
    }

    public boolean exists(Employee memberOne, Employee memberTwo){
        try{
            get(memberOne, memberTwo);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            onUserDelete(event.getUser());
        });
    }

    public void onUserDelete(User deletedUser) {
        if(deletedUser instanceof Employee chatMember){
            getAllChats(chatMember).forEach(chat->{
                this.delete(chat);
            });
        }
    }
}
