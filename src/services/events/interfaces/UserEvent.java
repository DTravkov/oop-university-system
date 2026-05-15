package services.events.interfaces;

import model.domain.User;

public interface UserEvent extends Event {
    public User getUser();
}
