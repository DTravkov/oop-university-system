package model.domain;

import java.util.*;

import utils.FieldValidator;
import utils.StringUtils;

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
		FieldValidator.requireSingleWord(name, "Name");
		FieldValidator.requireSingleWord(surname, "Surname");
		
    	this.login = login;
    	this.password = password;
    	this.name = StringUtils.capitalize(name);
    	this.surname = StringUtils.capitalize(surname);
    	this.isBanned = false;
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
		FieldValidator.requireNonBlank(name, "Surname");
		FieldValidator.requireSingleWord(name, "Surname");
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

	@Override
	public String asLine() {
		return String.format("ID: %d | Name: %s ",
				id, getFullname());
	}

	@Override
	public String asTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: ").append(id).append('\n');
		sb.append("Name: ").append(getFullname()).append('\n');
		return sb.toString();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id=" + this.getId() + ", name=" + this.getName() + ", surname=" + this.getSurname() +"]";
	}
	
	
	
	
	
    
    

}