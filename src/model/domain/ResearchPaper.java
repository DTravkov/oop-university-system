package model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ResearchPaper extends SerializableModel {
    private static final long serialVersionUID = 1L;

    private int views;
    private int citations;
    private Date publishDate;
    private List<User> researchers = new ArrayList<>();
    private List<ResearchPaper> references = new ArrayList<>();

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

    public List<User> getResearchers() {
        return List.copyOf(researchers);
    }

    public void setResearchers(List<User> researchers) {
        this.researchers = researchers != null ? new ArrayList<>(researchers) : new ArrayList<>();
    }

    public void addResearcher(User researcher) {
        if (researcher != null && !researchers.contains(researcher)) {
            researchers.add(researcher);
        }
    }

    public List<ResearchPaper> getReferences() {
        return List.copyOf(references);
    }

    public void setReferences(List<ResearchPaper> references) {
        this.references = references != null ? new ArrayList<>(references) : new ArrayList<>();
    }

    public void addReference(ResearchPaper paper) {
        if (paper != null && !references.contains(paper)) {
            references.add(paper);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResearchPaper that = (ResearchPaper) o;
        if (id != 0 && that.getId() != 0) {
            return id == that.getId();
        }
        return views == that.views
                && citations == that.citations
                && Objects.equals(publishDate, that.publishDate);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(views, citations, publishDate);
    }
}
