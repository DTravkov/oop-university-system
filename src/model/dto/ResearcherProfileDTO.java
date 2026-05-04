package model.dto;

import model.domain.ResearcherProfile;
import model.domain.User;

public final class ResearcherProfileDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final UserDTO user;

    public ResearcherProfileDTO(int id, UserDTO user) {
        super();
        if (id != 0) {
            setId(id);
        }
        this.user = user;
    }

    public ResearcherProfileDTO(ResearcherProfile profile, User user) {
        this(profile.getId(), new UserDTO(user));
    }

    public UserDTO getUser() {
        return user;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId() + " | User: " + formatUser(user);
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nUser: ").append(formatUser(user));
        return section("ResearcherProfile", body.toString());
    }
}
