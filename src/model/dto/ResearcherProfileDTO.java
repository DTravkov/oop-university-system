package model.dto;

import java.util.List;

import model.domain.ResearcherProfile;

public final class ResearcherProfileDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final UserDTO user;
    private final List<ResearchProjectDTO> researchProjects;

    public ResearcherProfileDTO(ResearcherProfile profile, UserDTO user, List<ResearchProjectDTO> researchProjects) {
        super();
        setId(profile.getId());
        this.user = user;
        this.researchProjects = List.copyOf(researchProjects);
    }

    public UserDTO getUser() {
        return user;
    }

    public List<ResearchProjectDTO> getResearchProjects() {
        return researchProjects;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId() + " | User: " + formatUser(user) + " | Projects: " + researchProjects.size();
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nUser: ").append(formatUser(user));
        body.append("\nResearchProjects: ").append(formatResearchProjectList(researchProjects));
        return section("ResearcherProfile", body.toString());
    }
}
