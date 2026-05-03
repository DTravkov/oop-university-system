package model.enumeration;

public enum TechRequestStatus {
    PENDING(1),
    REJECTED(2),
    ACCEPTED(3),
    DONE(4);

    private final int stage;

    private TechRequestStatus(int stage){
        this.stage = stage;
    }

    public int getStage() {
        return stage;
    }
}
