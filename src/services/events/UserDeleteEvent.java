package services.events;

import model.domain.User;

public record UserDeleteEvent(User user) implements Event{

    public User getUser(){
        return user;
    }

}
