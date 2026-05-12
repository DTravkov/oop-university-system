package model.domain;

import java.util.ArrayList;
import java.util.List;

import utils.FieldValidator;

public class ResearcherProfile extends SerializableModel {

    private User user;
    private List<ResearchPaper> papers = new ArrayList<>();

    public ResearcherProfile(User user) {
        FieldValidator.requireNonNull(user, "User");
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        FieldValidator.requireNonNull(user, "User");
        this.user = user;
    }

    public List<ResearchPaper> getPapers() {
        return List.copyOf(papers);
    }

    public void setPapers(List<ResearchPaper> papers) {
        this.papers = papers != null ? new ArrayList<>(papers) : new ArrayList<>();
    }

    public void addPaper(ResearchPaper paper) {
        FieldValidator.requireNonNull(paper, "Paper");
        if (!papers.contains(paper)) {
            papers.add(paper);
        }
    }

    public boolean removePaper(ResearchPaper paper) {
        return papers.remove(paper);
    }

    public boolean removePaper(int paperId) {
        return papers.removeIf(p -> p.getId() == paperId);
    }

    @Override
    public String asLine() {
        return String.format("ID: %d | Researcher: %s | Papers: %d",
                id, user.getFullname(), papers.size());
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append('\n');
        sb.append("/User/\n").append(user.asLine()).append('\n');
        sb.append("/Papers/\n");
        for (ResearchPaper p : papers) {
            sb.append(p.asLine()).append('\n');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ResearcherProfile{" +
                "id=" + id +
                ", user=" + user +
                ", papers=" + papers +
                '}';
    }
}
