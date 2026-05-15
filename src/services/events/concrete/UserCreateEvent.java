package services.events.concrete;

import model.domain.User;
import services.events.interfaces.UserEvent;

public record UserCreateEvent(User user) implements UserEvent{

    public User getUser(){
        return user;
    }


}
