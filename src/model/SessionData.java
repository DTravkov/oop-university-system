package model;

public class SessionData{
	private static SessionData instance = null;
	private static User user = null;
	
	private SessionData() {};
	
	public static void setUser(User user) {
		SessionData.user = user;
	}
	public static User getUser() {
		return user;
	}
	
	public SessionData getInstance() {
		if(instance == null) instance = new SessionData();
		return instance;
	}
	
}