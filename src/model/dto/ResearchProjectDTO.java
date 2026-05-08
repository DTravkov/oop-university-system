package model.dto;

import java.util.List;

import model.domain.ResearchProject;

public final class ResearchProjectDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final String topic;
    private final List<UserDTO> participants;

    public ResearchProjectDTO(ResearchProject project, List<UserDTO> participantDTOs) {
        super();
        setId(project.getId());
        this.topic = project.getTopic();
        this.participants = participantDTOs == null ? List.of() : List.copyOf(participantDTOs);
    }

    public String getTopic() {
        return topic;
    }

    public List<UserDTO> getParticipants() {
        return participants;
    }

    @Override
    public String toShortString() {
        String topicLabel = topic == null || topic.isEmpty() ? "_" : topic;
        return "ID: " + getId()
                + " | Topic: " + topicLabel
                + " | Participants: " + participants.size();
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nTopic: ").append(topic == null || topic.isEmpty() ? "_" : topic);
        body.append("\nParticipants: ").append(participants.isEmpty() ? "_" : formatUserList(participants));
        return section("Research Project", body.toString());
    }
}
