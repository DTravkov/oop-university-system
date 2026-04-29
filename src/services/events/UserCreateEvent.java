package services.events;

import model.domain.User;

public record UserCreateEvent(User user) implements Event{

    public int getUserId(){
        return user.getId();
    }

    public Class<? extends User> getUserClass(){
        return user.getClass();
    }

}
