package model.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import settings.AppSettings;
import utils.FieldValidator;
import utils.StringUtils;

public abstract class User extends SerializableModel{
	
    private static final long serialVersionUID = 1L;

    private String login;
    private String password;
    private String name;
    private String surname;
    private boolean isBanned = false;

	/**
	 * soft delete flag for user. it is used instead of DB deletion,
	 * so that we can restore any of deleted user data.
	 */
	private boolean isDeleted = false;

	/**
	 * map of notifications to boolean, boolean whether notification is read/unread.
	 */
	private Map<Notification, Boolean> notifications = new HashMap<>(); 


	public User(String login, String password, String name, String surname) {
    	FieldValidator.requireNonBlank(login);
    	FieldValidator.requireNonBlank(password);
    	FieldValidator.requireNonBlank(name);
    	FieldValidator.requireNonBlank(surname);
		FieldValidator.requireSingleWord(name);
		FieldValidator.requireSingleWord(surname);
		if(AppSettings.REQUIRE_PASSWORD_VALIDATION && !AppSettings.DEFAULT_SYSTEM_LOGINS.contains(this.getLogin()))
			FieldValidator.requirePasswordValidation(password);
		
    	this.login = login;
    	this.password = password;
    	this.name = StringUtils.capitalize(name);
    	this.surname = StringUtils.capitalize(surname);
    	this.isBanned = false;
    }



	public void addNotification(Notification notification){
		FieldValidator.requireNonNull(notification);
		this.notifications.put(notification, false); 
	}

	public boolean removeNotification(Notification notification){
		FieldValidator.requireNonNull(notification);
		Set<Entry<Notification, Boolean>> set = new HashSet<>();
		for(var entry : notifications.entrySet()){
			if(entry.getKey().equals(notification)){
				set.add(entry);
			}
		}
		set.forEach(entry -> notifications.remove(entry.getKey()));
		return set.size() > 0;
	}

	public Map<Notification, Boolean> getNotifications(){
		return Map.copyOf(notifications);
	}

	public List<Notification> getReadNotifications(){
		List<Notification> readNotifications = new ArrayList<>();
		notifications.entrySet().forEach(e -> {
			if(e.getValue() == true)
				readNotifications.add(e.getKey());
		});
		return List.copyOf(readNotifications);
	}

	public List<Notification> getUnreadNotifications(){
		List<Notification> unreadNotifications = new ArrayList<>();
		notifications.entrySet().forEach(e -> {
			if(e.getValue() == false)
				unreadNotifications.add(e.getKey());
		});
		return List.copyOf(unreadNotifications);
	}

	public void markNotificationsRead(){
		for(var entry : notifications.entrySet()){
			entry.setValue(true);
		}
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		FieldValidator.requireNonBlank(login);
		FieldValidator.requireSingleWord(login);
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		FieldValidator.requireNonBlank(password);
		FieldValidator.requireSingleWord(password);
		if(AppSettings.REQUIRE_PASSWORD_VALIDATION && !AppSettings.DEFAULT_SYSTEM_LOGINS.contains(this.getLogin()))
			FieldValidator.requirePasswordValidation(password);
        

		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		FieldValidator.requireNonBlank(name);
		FieldValidator.requireSingleWord(name);
		this.name = StringUtils.capitalize(name);
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		FieldValidator.requireNonBlank(surname);
		FieldValidator.requireSingleWord(surname);
		this.surname = surname;
	}

	public String getFullname() {
		return name + " " + surname;
	}

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean banned) {
		isBanned = banned;
	}

	/**
	 * checks that user is not banned or soft-deleted
	 * @return
	 */
	public boolean isAvailable() {
		return !(isBanned || isDeleted);
	}

	

	public boolean isDeleted() {
		return isDeleted;
	}

	public void markAsDeleted() {
		this.isDeleted = true;
	}

	public void unmarkAsDeleted() {
		this.isDeleted = false;
	}

	


	@Override
	public String asLine() {
		if(isDeleted){
			return String.format("ID: %d | Deleted User", id);
		}
		return String.format("ID: %d | Name: %s ",
				id, getFullname());
	}

	@Override
	public String asTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: ").append(id).append('\n');
		if(isDeleted){
			sb.append("Deleted User").append('\n');
			return sb.toString();
		}
		sb.append("Name: ").append(getFullname()).append('\n');
		return sb.toString();
	}

	/**
	 * this equals override is interesting. it allows checking users by id+subclass.
	 * so now, each user is compared as account in database, not plain java class
	 * interesting fact: previous versions of our project were comparing each class by .getId() == .getId(), real nightmare.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof User other)) return false;
		if (id == 0 || other.id == 0) return false;
		return id == other.id;
	}

		@Override
	public int hashCode() {
		return id != 0 ? Integer.hashCode(id) : super.hashCode();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id=" + this.getId() + ", name=" + this.getName() + ", surname=" + this.getSurname() +"]";
	}
	
	
	
	
	
    
    

}