package model.dto;

import model.domain.User;

public final class UserDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final String role;
    private final String login;
    private final String name;
    private final String surname;
    private final boolean banned;

    public UserDTO(User user) {
        super();
        setId(user.getId());
        this.role = user.getClass().getSimpleName();
        this.login = user.getLogin();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.banned = user.isBanned();
    }

    public String getRole() {
        return role;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isBanned() {
        return banned;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId() + " | Role: " + role + " | Name: " + name + " | Surname: " + surname;
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nRole: ").append(role);
        body.append("\nLogin: ").append(login);
        body.append("\nName: ").append(name);
        body.append("\nSurname: ").append(surname);
        body.append("\nBanned: ").append(banned);
        return section("User", body.toString());
    }
}
