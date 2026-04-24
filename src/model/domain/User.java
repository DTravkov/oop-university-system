package model.domain;

import java.util.*;

import utils.FieldValidator;

public class User extends SerializableModel{
	
    private static final long serialVersionUID = 1L;

    private String login;
    private String password;
    private String name;
    private String surname;
    private boolean isBanned = false;

    public User(User user) {
		this(user.getLogin(),user.getPassword(),user.getName(), user.getSurname());
    }
    public User(String login, String password, String name, String surname) {
    	FieldValidator.requireNonBlank(login, "Login");
    	FieldValidator.requireNonBlank(password, "Password");
    	FieldValidator.requireNonBlank(name, "Name");
    	FieldValidator.requireNonBlank(surname, "Surname");
		
    	this.login = login;
    	this.password = password;
    	this.name = name;
    	this.surname = surname;
    	this.isBanned = false;
    }

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean banned) {
		isBanned = banned;
	}


	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;

		if(id != 0) return id == user.getId();

		return Objects.equals(login, user.login);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, login);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id=" + this.getId() + ", name=" + this.getName() + ", surname=" + this.getSurname() +"]";
	}
	
	
	
	
	
    
    

}