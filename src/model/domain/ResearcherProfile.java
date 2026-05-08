package model.domain;

import java.util.ArrayList;
import java.util.List;

public class ResearcherProfile extends SerializableModel {

    private final int userId;
    private List<Integer> researchProjects = new ArrayList<>();

    public ResearcherProfile(int userId){
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public List<Integer> getResearchProjects() {
        return researchProjects;
    }

    public void addResearchProject(int reserachProjectId) {
        this.researchProjects.add(reserachProjectId);
    }
    
    public void removeResearchProject(int reserachProjectId) {
        this.researchProjects.remove(Integer.valueOf(reserachProjectId));
    }



    



}
