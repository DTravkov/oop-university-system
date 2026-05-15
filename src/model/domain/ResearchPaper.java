package model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import exceptions.AlreadyExists;

public class ResearchPaper extends SerializableModel {
    private static final long serialVersionUID = 1L;

    private String title;
    private int views;
    private int citations;
    private Date publishDate;
    private List<ResearcherProfile> researchers = new ArrayList<>();
    private List<ResearchPaper> references = new ArrayList<>();

    

    public ResearchPaper(String title) {
        this.title = title;
        this.views = 0;
        this.citations = 0;
        this.publishDate = new Date();
    }

    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getCitations() {
        return citations;
    }

    public void setCitations(int citations) {
        this.citations = citations;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public List<ResearchPaper> getReferences() {
        return List.copyOf(references);
    }
    
    public void addReference(ResearchPaper paper) {
        if (paper != null && !references.contains(paper)) {
            references.add(paper);
        }
    }

    public List<ResearcherProfile> getResearchers() {
        return List.copyOf(researchers);
    }

    public void addResearcher(ResearcherProfile researcher) {
        if (getResearchers().stream().anyMatch(r -> r.equals(researcher))) {
            throw new AlreadyExists("participant in paper id=" + getId());
        }
        if (!researchers.contains(researcher)) {
            researchers.add(researcher);
        }
    }

    public void removeResearcher(ResearcherProfile researcher) {
        if (researchers.contains(researcher)) {
            researchers.remove(researcher);
        }
    }

    @Override
    public String asLine() {
        return String.format("ID: %d | Title: %s | Views: %d | Citations: %d",
                id, title, views, citations);
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append('\n');
        sb.append("Title: ").append(title).append('\n');
        sb.append("Views: ").append(views).append('\n');
        sb.append("Citations: ").append(citations).append('\n');
        sb.append("Published: ").append(publishDate).append('\n');
        sb.append("/Researchers/\n");
        for (ResearcherProfile rp : researchers) {
            sb.append(rp.asLine()).append('\n');
        }
        sb.append("/Reference titles/\n");
        for (ResearchPaper ref : references) {
            sb.append(ref.getId()).append(" — ").append(ref.getTitle()).append('\n');
        }
        return sb.toString();
    }

}
