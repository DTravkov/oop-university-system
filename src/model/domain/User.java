package model.domain;

import java.util.ArrayList;
import java.util.List;

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
	private boolean isDeleted = false;

	private List<Notification> notifications = new ArrayList<>();

    public User(User user) {
		this(user.getLogin(),user.getPassword(),user.getName(), user.getSurname());
    }
    public User(String login, String password, String name, String surname) {
    	FieldValidator.requireNonBlank(login, "Login");
    	FieldValidator.requireNonBlank(password, "Password");
    	FieldValidator.requireNonBlank(name, "Name");
    	FieldValidator.requireNonBlank(surname, "Surname");
		FieldValidator.requireSingleWord(name, "Name");
		FieldValidator.requireSingleWord(surname, "Surname");
		if(AppSettings.REQUIRE_PASSWORD_VALIDATION)
			FieldValidator.requirePasswordValidation(password);
		
    	this.login = login;
    	this.password = password;
    	this.name = StringUtils.capitalize(name);
    	this.surname = StringUtils.capitalize(surname);
    	this.isBanned = false;
    }



	public void addNotification(Notification notification){
		FieldValidator.requireNonNull(notification, "Notification");
		this.notifications.add(notification);
	}

	public boolean removeNotification(Notification notification){
		FieldValidator.requireNonNull(notification, "Notification");
		return this.notifications.remove(notification);
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		FieldValidator.requireNonBlank(login, "Login");
		FieldValidator.requireSingleWord(login, "Login");
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		FieldValidator.requireNonBlank(password, "Password");
		FieldValidator.requireSingleWord(password, "Password");
		if(AppSettings.REQUIRE_PASSWORD_VALIDATION)
			FieldValidator.requirePasswordValidation(password);
        

		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		FieldValidator.requireNonBlank(name, "Name");
		FieldValidator.requireSingleWord(name, "Name");
		this.name = StringUtils.capitalize(name);
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		FieldValidator.requireNonBlank(surname, "Surname");
		FieldValidator.requireSingleWord(surname, "Surname");
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