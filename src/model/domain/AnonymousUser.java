package model.domain;

import exceptions.ImmutableFieldChanged;
import settings.AppSettings;

public class AnonymousUser extends User{

	private static final long serialVersionUID = 1L;

	public AnonymousUser() {
		super("ANONYMOUS", "hello, anonymous!", "Anonymous", "User");
		this.id = AppSettings.ANONYMOUS_USER_ID;
	}

	@Override
	public void setId(int id) {
		throw new ImmutableFieldChanged();
	}

}