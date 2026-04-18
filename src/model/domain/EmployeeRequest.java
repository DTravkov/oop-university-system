package model.domain;

import java.util.Objects;

public class EmployeeRequest extends Message {

    private static final long serialVersionUID = 1L;

    private boolean isApprovedByDean;

    public EmployeeRequest(User sender, User receiver, String content) {
        super(sender, receiver, content);
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
                "sender=" + getSender() +
                "content=" + getContent() +
                '}';
    }
}
