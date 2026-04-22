package model.domain;

public class EmployeeRequest extends Message {

    private static final long serialVersionUID = 1L;

    private boolean isApprovedByDean;

    public EmployeeRequest(int senderId, int receiverId, String content) {
        super(senderId, receiverId, content);
        this.isApprovedByDean = false;
    }

    public boolean isApprovedByDean() {
        return isApprovedByDean;
    }

    public void setApprovedByDean(boolean approvedByDean) {
        isApprovedByDean = approvedByDean;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EmployeeRequest that = (EmployeeRequest) o;
        return isApprovedByDean == that.isApprovedByDean;
    }

    @Override
    public String toString() {
        return "EmployeeRequest{" +
                "senderId=" + getSenderId() +
                "content=" + getContent() +
                '}';
    }
}
