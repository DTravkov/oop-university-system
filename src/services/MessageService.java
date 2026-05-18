package services;

import java.util.List;

import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.Chat;
import model.domain.Employee;
import model.domain.Message;
import model.domain.User;
import services.events.concrete.UserDeleteEvent;
import utils.Logger;
import utils.UIText;

/**
 * MessageService is a concrete service. It implements logic for employee chats: sending messages inside a chat,
 * opening a chats between two employees, and removing chats when a participant is deleted.
 */
public class MessageService extends GenericService<Chat>{


    public MessageService() {
        super(Chat.class);
    }

    // CREATE / UPDATE / DELETE (SENDING MUTATES CHAT STATE)

    // has a few overloads to send messages with different sets of data.
    public Chat sendMessage(Chat chat, Message msg) {
        Employee sender = (Employee) msg.getSender();
        Employee receiver = (Employee) chat.getOtherMember(sender);

        if(!sender.isAvailable() || !receiver.isAvailable()){
            throw new OperationNotAllowed(UIText.ERR_MESSAGE_DELETED_ACCOUNT);
        }

        if (!chat.isMember(sender)) {
            throw new DoesNotExist(UIText.ERR_CHAT_SENDER_NOT_MEMBER);
        }
        
        chat.sendMessage(msg);
        Logger.log("Send message by sender (" + sender.asLine() + ") in chat (" + chat.getId() + ")");
        repository.save(chat);
        return chat;
    }

    public Chat sendMessage(Message msg, Employee to) {

        Employee sender = (Employee) msg.getSender();
        Employee receiver = (Employee) to;

        if(!sender.isAvailable() || !receiver.isAvailable()){
            throw new OperationNotAllowed(UIText.ERR_MESSAGE_DELETED_ACCOUNT);
        }
        
        Chat chat;

        if(exists(sender, receiver)){
            chat = getChatByMembers(sender, receiver);
            chat.sendMessage(msg);
            Logger.log("Send message by sender (" + sender.asLine() + ") to receiver (" + receiver.asLine() + ") in chat (" + chat.getId() + ")");
            update(chat);
            return chat;
        }

        chat = this.create(new Chat(sender, receiver));
        chat.sendMessage(msg);
        Logger.log("Send message by sender (" + sender.asLine() + ") to receiver (" + receiver.asLine() + ") in chat (" + chat.getId() + ")");
        repository.save(chat);
        return chat;
    }


    // QUERIES

    public List<Chat> getChatsByMember(Employee chatMember){
        return getAll(ch -> ch.isMember(chatMember));
    }

    public Chat getChatByMembers(Employee memberOne, Employee memberTwo){
        return getAll().stream()
                       .filter(chat -> chat.isMember(memberOne) && chat.isMember(memberTwo))
                       .findFirst()
                       .orElseThrow(() -> new DoesNotExist("No chat exists between these two users."));
    }

    public boolean exists(Employee memberOne, Employee memberTwo){
        try{
            getChatByMembers(memberOne, memberTwo);
            return true;
        }
        catch (DoesNotExist e){
            return false;
        }
    }


    // EVENT HANDLING

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            onUserDelete(event.getUser());
        });
    }

    public void onUserDelete(User deletedUser) {
        if(deletedUser instanceof Employee chatMember){
            getChatsByMember(chatMember).forEach(chat->{
                this.delete(chat);
            });
        }
    }
}
