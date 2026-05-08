package model.dto;

import java.util.Date;
import java.util.List;

import model.domain.ResearchPaper;

public final class ResearchPaperDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final List<UserDTO> participants;
    private final int views;
    private final int citations;
    private final Date publishDate;

    public ResearchPaperDTO(ResearchPaper paper, List<UserDTO> participantDTOs) {
        super();
        setId(paper.getId());
        this.participants = participantDTOs == null ? List.of() : List.copyOf(participantDTOs);
        this.views = paper.getViews();
        this.citations = paper.getCitations();
        this.publishDate = paper.getPublishDate();
    }

    public List<UserDTO> getParticipants() {
        return participants;
    }

    public int getViews() {
        return views;
    }

    public int getCitations() {
        return citations;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId()
                + " | Views: " + views
                + " | Citations: " + citations
                + " | Participants: " + participants.size();
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nParticipants: ").append(participants.isEmpty() ? "_" : formatUserList(participants));
        body.append("\nViews: ").append(views);
        body.append("\nCitations: ").append(citations);
        body.append("\nPublished: ").append(publishDate == null ? "_" : formatDate(publishDate));
        return section("Research Paper", body.toString());
    }
}
