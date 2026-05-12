package model.domain;

import settings.AppSettings;

public class DeletedUser extends User{
	// placeholder that represents deleted user.
	private static final long serialVersionUID = 1L;

    public DeletedUser() {
		super("DELETED_USER", "Deleted", "Deleted", "User");
		this.setBanned(true);
		this.id = AppSettings.DELETED_USER_ID;
	}

	@Override
	public String asLine() {
		return "ID: " + id + " | Deleted User";
	}

	@Override
	public String asTable() {
		return "\nDeleted User\n";
	}

}