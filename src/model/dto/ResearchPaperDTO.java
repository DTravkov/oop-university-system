package model.dto;

import model.domain.ResearchPaper;

public final class ResearchPaperDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    public ResearchPaperDTO(int id) {
        super();
        if (id != 0) {
            setId(id);
        }
    }

    public ResearchPaperDTO(ResearchPaper paper) {
        this(paper.getId());
    }

    @Override
    public String toShortString() {
        return "ID: " + getId() + " | Type: ResearchPaper";
    }

    @Override
    public String toString() {
        return section("ResearchPaper", "\nID: " + getId());
    }
}
