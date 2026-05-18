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
    /**
     * wrapper for any notification, helps to send it based on its type (multicast or unicast).
     * this method is called each time {@link NotificationCreateEvent} is published to {@link EventSystem}
     * @param notification
     */
    public void sendNotification(Notification notification){
        if (notification.isUnicast()) {
            unicast(notification);
        } else if (notification.isMulticast()) {
            multicast(notification);
        }
    }

    public void markNotificationsRead(User user){
        user.markNotificationsRead();
        userService.update(user);
    }

    /**
     * this function sends a notification to all users who extend {@link Notification.multicastClass} OR listed in {@link Notification.receivers}.
     * {@link Notification.multicastClass} also sends notification to its subclasses.
     * Multicast can also be broadcast, if {@link Notification.multicastClass} == User.class.
     * @param notification
     */
    private void multicast(Notification notification){
        // create notification record in database
        repository.save(notification);

        if(!notification.getReceivers().isEmpty()){
            notification.getReceivers()
                .forEach(u -> u.addNotification(notification));
        }
        else if(notification.getMulticastClass() != null){
            getMulticastReceivers(notification)
                .forEach(u -> u.addNotification(notification));
        }

        //saveAll saves all models in whole system
        repository.saveAll();
    }

    /**
     * this function sends a notification to only one user, who is Notification.getReceiver() 
     * @param notification
     */
    private void unicast(Notification notification){
        repository.save(notification);
        notification.getReceiver().addNotification(notification);
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
            if(notification.getMulticastClass() != null){
                List<User> receiverList = getMulticastReceivers(notification);
                receiverList.forEach(r -> r.removeNotification(notification));
            }
            else if(!notification.getReceivers().isEmpty()){
                List<User> receivers = notification.getReceivers();
                receivers.forEach(r -> r.removeNotification(notification));
            }
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
        if(notification != null){
            sendNotification(notification);
        }
        
    }

    private void onNotificationDelete(Notification notification){
        if(notification != null){
            recall(notification);
        }
        
    }






    

}
