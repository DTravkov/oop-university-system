package model.domain;

import java.util.Date;
import java.util.Objects;

import utils.FieldValidator;

public class EmployeeRequest extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private User sender;
    private User receiver;
    private String content;
    private Date requestDate;
    private boolean isApprovedByDean;

    public EmployeeRequest(User sender, User receiver, String content) {
        FieldValidator.requireNonNull(sender, "Sender");
        FieldValidator.requireNonNull(receiver, "Receiver");
        FieldValidator.requireNonBlank(content, "Content");
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.requestDate = new Date();
        this.isApprovedByDean = false;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        FieldValidator.requireNonNull(sender, "Sender");
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        FieldValidator.requireNonNull(receiver, "Receiver");
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        FieldValidator.requireNonBlank(content, "Content");
        this.content = content;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        FieldValidator.requireNonNull(requestDate, "Request date");
        this.requestDate = requestDate;
    }

    public boolean isApprovedByDean() {
        return isApprovedByDean;
    }

    public void setApprovedByDean(boolean approvedByDean) {
        isApprovedByDean = approvedByDean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeRequest that = (EmployeeRequest) o;
        if (id != 0 && that.getId() != 0) {
            return id == that.getId();
        }
        return isApprovedByDean == that.isApprovedByDean
                && Objects.equals(sender, that.sender)
                && Objects.equals(receiver, that.receiver)
                && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(sender, receiver, content, isApprovedByDean);
    }

    @Override
    public String toString() {
        return "EmployeeRequest{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", content=" + content +
                ", approved=" + isApprovedByDean +
                '}';
    }
}
