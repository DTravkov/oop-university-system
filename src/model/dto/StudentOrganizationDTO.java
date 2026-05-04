package model.dto;

import model.domain.StudentOrganization;
import model.domain.User;

import java.util.List;

public final class StudentOrganizationDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final UserDTO president;
    private final List<UserDTO> members;

    public StudentOrganizationDTO(int id, String name, String description, UserDTO president,
                                  List<UserDTO> members) {
        super();
        if (id != 0) {
            setId(id);
        }
        this.name = name;
        this.description = description;
        this.president = president;
        this.members = members == null ? List.of() : List.copyOf(members);
    }

    public StudentOrganizationDTO(StudentOrganization organization, User president, List<UserDTO> members) {
        this(
                organization.getId(),
                organization.getName(),
                organization.getDescription(),
                new UserDTO(president),
                members);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UserDTO getPresident() {
        return president;
    }

    public List<UserDTO> getMembers() {
        return members;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId()
                + " | Name: " + name
                + " | President: " + formatUser(president)
                + " | Member count: " + members.size();
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nName: ").append(name);
        body.append("\nDescription: ").append(description);
        body.append("\nPresident: ").append(formatUser(president));
        body.append("\nMembers: ").append(formatUserList(members));
        return section("StudentOrganization", body.toString());
    }
}
