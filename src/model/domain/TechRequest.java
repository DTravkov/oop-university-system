package model.domain;

import exceptions.OperationNotAllowed;
import model.enumeration.TechRequestStatus;
import utils.FieldValidator;

public class TechRequest extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private TechRequestStatus status;
    private final Employee employee;
    private TechSupportSpecialist specialist;
    private String content;

    public TechRequest(Employee employee, TechSupportSpecialist specialist, String content) {
        FieldValidator.requireNonNull(employee);
        FieldValidator.requireNonNull(specialist);
        FieldValidator.requireNonBlank(content);
        if(specialist.equals(employee)){
            throw new OperationNotAllowed("You cannot send a tech request to yourself.");
        }
        this.employee = employee;
        this.specialist = specialist;
        this.content = content;
        this.status = TechRequestStatus.PENDING;
    }

    public TechRequestStatus getStatus() {
        return status;
    }

    public void setStatus(TechRequestStatus status) {
        FieldValidator.requireNonNull(status);
        if(status.getStage() < getStatus().getStage() || status.equals(getStatus())){
            throw new OperationNotAllowed("Tech request status is already set to this value.");
        }
        this.status = status;
    }

    public Employee getEmployee() {
        return employee;
    }

    public TechSupportSpecialist getSpecialist() {
        return specialist;
    }

    public void setSpecialist(TechSupportSpecialist specialist) {
        FieldValidator.requireNonNull(specialist);
        this.specialist = specialist;
    }

    public String getContent() {
        return content;
    }


    @Override
    public String asLine() {
        return String.format("ID: %d | Status: %s | From: %s | To: %s | Content: %s",
                id, status, employee.getFullname(), specialist.getFullname(), content);
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append('\n');
        sb.append("Status: ").append(status).append('\n');
        sb.append("/Employee/\n").append(employee.asLine()).append('\n');
        sb.append("/Specialist/\n").append(specialist.asLine()).append('\n');
        sb.append("Content:\n").append(content).append('\n');
        return sb.toString();
    }

    @Override
    public String toString() {
        return "TechRequest{" +
                "id=" + id +
                ", employee=" + employee +
                ", specialist=" + specialist +
                ", content='" + content + '\'' +
                ", status=" + status +
                '}';
    }
}
