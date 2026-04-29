package model.enumeration;

public enum NewsUrgencyLevel  {
    RESEARCH(1),
    HIGH(2),
    AVERAGE(3),
    LOW(4);

    private final int priority;
    private NewsUrgencyLevel(int priority) {
        this.priority = priority;
    }

    public int getPriority(){
        return priority;
    }
}
