package services.events;

import model.domain.User;

public record UserDeleteEvent(User user) implements Event{

    public int getUserId(){
        return user.getId();
    }

    public Class<? extends User> getUserClass(){
        return user.getClass();
    }

}
