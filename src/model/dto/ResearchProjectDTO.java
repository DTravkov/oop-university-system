package model.dto;

import model.domain.ResearchProject;

public final class ResearchProjectDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    public ResearchProjectDTO(int id) {
        super();
        if (id != 0) {
            setId(id);
        }
    }

    public ResearchProjectDTO(ResearchProject project) {
        this(project.getId());
    }

    @Override
    public String toShortString() {
        return "ID: " + getId() + " | Type: ResearchProject";
    }

    @Override
    public String toString() {
        return section("ResearchProject", "\nID: " + getId());
    }
}
