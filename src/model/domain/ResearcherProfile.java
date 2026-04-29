package model.domain;

public class ResearcherProfile extends SerializableModel {

    private final int userId;

    public ResearcherProfile(int userId){
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    

}
