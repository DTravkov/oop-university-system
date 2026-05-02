package model.domain;

import exceptions.ImmutableFieldChanged;
import settings.AppSettings;

public class DeletedUser extends User{
	// placeholder that represents deleted user.
	private static final long serialVersionUID = 1L;

    public DeletedUser() {
		super("DELETED", "DELETED", "DELETED", "USER");
		this.setBanned(true);
		this.id = AppSettings.DELETED_USER_ID;
	}    

	@Override
	public void setId(int id) {
		throw new ImmutableFieldChanged();
	}

}