package model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public int getUserId() {
        return user.getId();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResearcherProfile that = (ResearcherProfile) o;
        if (id != 0 && that.getId() != 0) {
            return id == that.getId();
        }
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(user);
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
