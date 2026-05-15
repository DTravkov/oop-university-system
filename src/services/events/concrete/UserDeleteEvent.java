package services.events.concrete;

import model.domain.User;
import services.events.interfaces.UserEvent;

public record UserDeleteEvent(User user) implements UserEvent{

    public User getUser(){
        return user;
    }

}
