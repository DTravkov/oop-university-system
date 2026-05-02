package model.domain;

import exceptions.ImmutableFieldChanged;
import settings.AppSettings;

public class SystemUser extends User{

	private static final long serialVersionUID = 1L;

	public SystemUser() {
		super("SYSTEM", "hello, system!", "USER", "SYSTEM");
		this.id = AppSettings.SYSTEM_USER_ID;
	}

	@Override
	public void setId(int id) {
		throw new ImmutableFieldChanged();
	}

}