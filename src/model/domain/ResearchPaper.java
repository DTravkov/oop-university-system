package model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import utils.FieldValidator;
import utils.UIText;

public class ResearchPaper extends SerializableModel {
    private static final long serialVersionUID = 1L;

    private String title;
    private int views;
    private int citations;
    private Date publishDate;
    private List<ResearcherProfile> researchers = new ArrayList<>();
    private List<ResearchPaper> references = new ArrayList<>();

    public ResearchPaper(String title) {
        FieldValidator.requireNonBlank(title);
        this.title = title;
        this.views = 0;
        this.citations = 0;
        this.publishDate = new Date();
    }

    public void setTitle(String title) {
        FieldValidator.requireNonBlank(title);
        this.title = title;
    }

    public String getTitle() {
        return title;
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

    public void setPublishDate(Date publishDate) {
        FieldValidator.requireNonNull(publishDate);
        this.publishDate = publishDate;
    }

    public List<ResearchPaper> getReferences() {
        return List.copyOf(references);
    }

    public void addReference(ResearchPaper reference) {
        FieldValidator.requireNonNull(reference);
        if (this.equals(reference)) {
            throw new OperationNotAllowed(UIText.ERR_PAPER_SELF_REFERENCE);
        }
        if (references.contains(reference)) {
            throw new AlreadyExists(UIText.ERR_PAPER_REFERENCE_EXISTS);
        }
        references.add(reference);
    }

    public List<ResearcherProfile> getResearchers() {
        return List.copyOf(researchers);
    }

    public ResearcherProfile addResearcher(ResearcherProfile researcher) {
        FieldValidator.requireNonNull(researcher);

        if (!researchers.contains(researcher)) {
            researchers.add(researcher);
        }
        if (!researcher.getPapers().contains(this)) {
            researcher.addPaper(this);
        }

        return researcher;
    }

    public void removeResearcher(ResearcherProfile researcher) {
        FieldValidator.requireNonNull(researcher);
        if(researchers.contains(researcher)){
            researchers.remove(researcher);
        }
        if(researcher.getPapers().contains(this)){
            researcher.removePaper(this);
        }
        
    }

    public void cite() {
        citations++;
    }

    public void incrementViews() {
        views++;
    }

    @Override
    public String asLine() {
        return String.format("ID: %d | Title: %s | Researchers: %s | Views: %d | Citations: %d",
                id, title, researchers.size(), views, citations);
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
