package services;

import java.util.List;

import exceptions.DoesNotExist;
import model.domain.Notification;
import model.domain.User;

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
     * this function sends a notification to all users who extend receiverClass. To both receiverClass and its subclassess.
     * @param notification
     * @param receiverClass
     */

    public void broadcast(Notification notification){
        if(notification.isUnicast())
            throw new RuntimeException("You cant send unicast notifications as multicast");
        repository.save(notification);

        getMulticastReceivers(notification)
            .forEach(u -> u.addNotification(notification));

        //saveAll also saves other models, users in this case
        repository.saveAll();
    }

    public void multicast(Notification notification){
        if(notification.isUnicast())
            throw new RuntimeException("You cant send unicast notifications as multicast");
        repository.save(notification);

        getMulticastReceivers(notification)
            .forEach(u -> u.addNotification(notification));

        //saveAll also saves other models, users in this case
        repository.saveAll();
    }

    public void unicast(Notification notification){
        if(notification.isMulticast())
            throw new RuntimeException("You cant send multicast notifications as unicast");
        User receiver = userService.get(notification.getReceiver());
        repository.save(notification);
        receiver.addNotification(notification);
        repository.saveAll();
    }

    public void recall(Notification notification){
        if(repository.find(n -> n.equals(notification)) == null){
            throw new DoesNotExist("Notification with id=" + notification.getId());
        }
        if(notification.isUnicast()){
            User unicast = getUnicastReceiver(notification);
            if(unicast != null){
                unicast.removeNotification(notification);
            }
        }else if(notification.isMulticast()){
            List<User> multicast = getMulticastReceivers(notification);
            multicast.forEach(u -> u.removeNotification(notification));
        }
        repository.delete(notification);
        repository.saveAll();
    }


    @SuppressWarnings("unchecked")
    private List<User> getMulticastReceivers(Notification notification){
        return (List<User>) userService.getUsersByClass(notification.getMulticastGroupClass());
    }

    private User getUnicastReceiver(Notification notification){
        return userService.find(u -> u.equals(notification.getReceiver()));
    }






    

}
