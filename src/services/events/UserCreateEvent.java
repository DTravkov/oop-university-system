package services.events;

import model.domain.User;

public record UserCreateEvent(User user) implements Event{

    public User getUser(){
        return user;
    }


}
