package services.events.concrete;



import model.domain.Notification;
import services.events.interfaces.NotificationEvent;

public record NotificationCreateEvent(Notification notification) implements NotificationEvent{

    public Notification getNotification(){
        return notification;
    }

}
