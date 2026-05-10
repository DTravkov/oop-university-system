package model.domain;

import settings.AppSettings;

public class AnonymousUser extends User{
	// placeholder that represents unauthorized user.
	private static final long serialVersionUID = 1L;

    public AnonymousUser() {
        // Non-blank placeholder required by User constructor validation (password cannot be empty).
        super("Anonymous", "anonymous", "Anonymous", "User");
		this.setBanned(true);
		this.id = AppSettings.ANONYMOUS_USER_ID;
	}

	

    

}