package model.domain;

import java.util.ArrayList;
import java.util.List;

public class ResearchProject extends SerializableModel {
    private static final long serialVersionUID = 1L;

    private String topic;
    private List<Integer> participants;
    private List<Integer> papers;

    public ResearchProject(String topic){
        this.topic = topic;
        this.participants = new ArrayList<>();
        this.papers = new ArrayList<>();
    }

    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }


    public List<Integer> getParticipants() {
        return List.copyOf(participants);
    }

    public void addParticipant(int userId) {
        this.participants.add(userId);
    }

    public void removeParticipant(int userId) {
        this.participants.remove(Integer.valueOf(userId));
    }
    
    public List<Integer> getPapers() {
        return List.copyOf(this.papers);
    }

    public void addPaper(int paperId) {
        this.papers.add(paperId);
    }

    public void removePaper(int paperId) {
        this.papers.remove(paperId);
    }

}
