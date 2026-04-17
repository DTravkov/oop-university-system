package model;

import java.io.*;
import java.util.*;

public class User implements Serializable, Indexed {
	
    private static final long serialVersionUID = 1L;
    
	private int id;
    private String login;
    private String password;
    private String name;
    private String surname;
    private boolean isBanned = false;


    public User(String login, String password, String name, String surname) {
    	this.login = login;
    	this.password = password;
    	this.name = name;
    	this.surname = surname;
    	this.isBanned = false;
    }

	public User(User user) {
    	this.login = user.getLogin();
    	this.password = user.getPassword();
    	this.name = user.getName();
    	this.surname = user.getSurname();
    	this.isBanned = user.isBanned();
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
	
	
	
    
    

}