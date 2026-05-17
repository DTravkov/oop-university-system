package model.domain;


public class ProfileStatus extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private final User user;
    private String status;

    public ProfileStatus(User user, String status) {
        this.user = user;
        this.status = status;
    }

    public User getUser(){
        return user;
    }

    public String getStatus(){
        return this.status;
    }

    public void setStatus(String newStatus){
        this.status = newStatus;
    }

    @Override
    public String asLine() {
        return this.status;
    }

    @Override
    public String asTable() {
        return this.id + "\n"  + this.status;
    }

}
