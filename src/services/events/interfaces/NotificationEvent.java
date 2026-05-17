package services.events.interfaces;

import model.domain.Notification;

public interface NotificationEvent extends Event {
    public Notification getNotification();
}
