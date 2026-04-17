package model;

import java.io.*;
import java.util.*;

import utils.FieldValidator;

public class User implements Serializable, Indexed {
	
    private static final long serialVersionUID = 1L;
    
	private int id;
    private String login;
    private String password;
    private String name;
    private String surname;
    private boolean isBanned = false;

    public User(User user) {
    	this(user.getLogin(),user.getPassword(),user.getName(), user.getPassword());
    }
    public User(String login, String password, String name, String surname) {
    	FieldValidator validator = new FieldValidator();
    	validator.requireNonBlank(login, "Login");
    	validator.requireNonBlank(password, "Password");
    	validator.requireNonBlank(name, "Name");
    	validator.requireNonBlank(surname, "Surname");
		validator.validate();
		
    	this.login = login;
    	this.password = password;
    	this.name = name;
    	this.surname = surname;
    	this.isBanned = false;
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

	public void setBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}
	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}



	@Override
	public int hashCode() {
		return Objects.hash(id, isBanned, login, name, password, surname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return id == other.id && isBanned == other.isBanned && Objects.equals(login, other.login)
				&& Objects.equals(name, other.name) && Objects.equals(password, other.password)
				&& Objects.equals(surname, other.surname);
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", login=" + login + ", password=" + password + ", name=" + name + ", surname="
				+ surname + ", isBanned=" + isBanned + "]";
	}
	
	
	
	
	
    
    

}