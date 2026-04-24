package model.domain;

public class DeletedUser extends User{
	// placeholder for deleted users' data.
	public static final int ID = -1;
    public DeletedUser() {
		super("DELETED", "DELETED", "DELETED", "USERS");
		this.setBanned(true);
		this.id = ID;
	}

	private static final long serialVersionUID = 1L;
	

    

}