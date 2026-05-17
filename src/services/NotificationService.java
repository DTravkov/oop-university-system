package services;

import java.util.List;

import model.domain.Notification;
import model.domain.User;
import services.events.concrete.NotificationCreateEvent;
import services.events.concrete.NotificationDeleteEvent;

/**
 * NotificationService is a helper service that listens to events,and sends notifications to user when needed.
 */
public class NotificationService extends BaseService<Notification> {

    private final UserService userService;

    public NotificationService(UserService userService) {
        super(Notification.class);
        this.userService = userService;
    }

    public void sendNotification(Notification notification){
        if(notification.isMulticast()){
            multicast(notification);
        }else if(notification.isUnicast()){
            unicast(notification);
        }
    }

    public void markNotificationsRead(User user){
        user.markNotificationsRead();
        userService.update(user);
    }

    /**
     * this function sends a notification to all users who extend receiverClass of the Notification. 
     * To both receiverClass and its subclassess, actually)
     * Multicast can also be broadcast, if Notification.receiverClass == User.class.
     * @param notification
     */
    private void multicast(Notification notification){
        repository.save(notification);

        getMulticastReceivers(notification)
            .forEach(u -> u.addNotification(notification));

        //saveAll also saves other models, users in this case
        repository.saveAll();
    }

    /**
     * this function sends a notification to only one user, who is Notification.getReceiver() 
     * @param notification
     */
    private void unicast(Notification notification){
        User receiver = userService.get(notification.getReceiver());
        repository.save(notification);
        receiver.addNotification(notification);
        repository.saveAll();
    }

    /**
     * this function deletes a notification if its already sent, 
     * from all users who got notified.
     * @param notification
     * @param receiverClass
     */
    private void recall(Notification notification){
        if(notification.isUnicast()){
            User receiver = notification.getReceiver();
            if(receiver != null){
                receiver.removeNotification(notification);
            }
        }else if(notification.isMulticast()){
            List<User> receiverList = getMulticastReceivers(notification);
            receiverList.forEach(r -> {
                if(r != null){
                    r.removeNotification(notification);
                }
            });
        }
        repository.delete(notification);
        repository.saveAll();
    }


    @SuppressWarnings("unchecked")
    private List<User> getMulticastReceivers(Notification notification){
        Class<?> mutlicastClass = notification.getMulticastClass();
        if(mutlicastClass == null) 
            return List.of();
        return (List<User>) userService.getUsersByClass(notification.getMulticastClass());
    }

    @Override
    protected void subscribeToEvents() {
        eventSystem.subscribe(NotificationCreateEvent.class, 
            (event) -> onNotificationCreate(event.getNotification()));
            eventSystem.subscribe(NotificationDeleteEvent.class, 
                (event) -> onNotificationDelete(event.getNotification()));
    }

    private void onNotificationCreate(Notification notification){
        sendNotification(notification);
    }

    private void onNotificationDelete(Notification notification){
        recall(notification);
    }






    

}
