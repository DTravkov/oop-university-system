package model.dto;

import java.util.List;

import model.domain.ResearchProject;

public final class ResearchProjectDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final String topic;
    private final List<UserDTO> participants;
    private final List<ResearchPaperDTO> papers;

    public ResearchProjectDTO(ResearchProject project, List<UserDTO> participantDTOs, List<ResearchPaperDTO> paperDTOs) {
        super();
        setId(project.getId());
        this.topic = project.getTopic();
        this.participants = participantDTOs == null ? List.of() : List.copyOf(participantDTOs);
        this.papers = paperDTOs == null ? List.of() : List.copyOf(paperDTOs);
    }

    public String getTopic() {
        return topic;
    }

    public List<UserDTO> getParticipants() {
        return participants;
    }

    public List<ResearchPaperDTO> getPapers() {
        return papers;
    }

    @Override
    public String toShortString() {
        String topicLabel = topic == null || topic.isEmpty() ? "_" : topic;
        return "ID: " + getId()
                + " | Topic: " + topicLabel
                + " | Participants: " + participants.size()
                + " | Papers: " + papers.size();
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nTopic: ").append(topic == null || topic.isEmpty() ? "_" : topic);
        body.append("\nParticipants: ").append(participants.isEmpty() ? "_" : formatUserList(participants));
        body.append("\nPapers: ").append(papers.isEmpty() ? "_" : papers.stream()
                .map(ResearchPaperDTO::toShortString)
                .toList());
        return section("ResearchProject", body.toString());
    }
}
