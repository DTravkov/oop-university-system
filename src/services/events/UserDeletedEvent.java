package services.events;

import model.domain.users.User;

public record UserDeletedEvent(User user) implements Event{

    public int getUserId(){
        return user.getId();
    }

    public Class<? extends User> getUserClass(){
        return user.getClass();
    }
    
}
