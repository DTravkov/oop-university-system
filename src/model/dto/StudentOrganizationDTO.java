package model.dto;

import model.domain.StudentOrganization;

import java.util.List;

public final class StudentOrganizationDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final UserDTO president;
    private final List<UserDTO> members;

    public StudentOrganizationDTO(StudentOrganization organization, UserDTO president, List<UserDTO> members) {
        super();
        setId(organization.getId());
        this.name = organization.getName();
        this.description = organization.getDescription();
        this.president = president;
        this.members = members == null ? List.of() : List.copyOf(members);
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
