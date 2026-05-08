package model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResearchPaper extends SerializableModel {
    private static final long serialVersionUID = 1L;

    private List<String> pages = new ArrayList<>();
    private List<Integer> participants = new ArrayList<>();
    private int views = 0;
    private int citations = 0;
    private Date publishDate = new Date();
    

    public ResearchPaper() {
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

    public int getViews() {
        return views;
    }
    
    public void addView(){
        this.views += 1;
    };

    public int getCitations() {
        return citations;
    }

    public void addCitation(){
        this.citations += 1;
    };

    public Date getPublishDate() {
        return publishDate;
    }

    public void addPage(String pageContent, int pageNumber){
        this.pages.add(pageNumber, pageContent);
    }

    public void appendPage(String pageContent){
        this.pages.add(pageContent);
    }

    public void popPage(){
        this.pages.removeLast();
    }
    public void removePage(int pageNumber){
        this.pages.remove(pageNumber);
    }
    
    
}
