package model.domain;

import java.util.ArrayList;
import java.util.List;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
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

    public ResearchPaper addPaper(ResearchPaper paper) {
        if(!papers.contains(paper)){
            this.papers.add(paper);
        }
        if(!paper.getResearchers().contains(this)){
            paper.addResearcher(this);
        }

        return paper;
    }

    public void removePaper(ResearchPaper paper) {
        if(this.papers.contains(paper)){
            this.papers.remove(paper);
        }
        if(paper.getResearchers().contains(this)){
            paper.removeResearcher(this);
        }
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
        sb.append("Main Profile:\n").append(user.asLine()).append('\n');
        sb.append("/Papers/\n");
        for (ResearchPaper p : papers) {
            sb.append(p.asTable()).append('\n');
        }
        return sb.toString();
    }

}
