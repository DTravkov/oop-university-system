package model.domain;

import model.enumeration.TechRequestStatus;
import utils.FieldValidator;

public class TechRequest extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private TechRequestStatus status;
    private final Employee employee;
    private TechSupportSpecialist specialist;
    private String content;

    public TechRequest(Employee employee, TechSupportSpecialist specialist, String content) {
        FieldValidator.requireNonNull(employee, "Employee");
        FieldValidator.requireNonNull(specialist, "Specialist");
        FieldValidator.requireNonBlank(content, "Content");
        this.employee = employee;
        this.specialist = specialist;
        this.content = content;
        this.status = TechRequestStatus.PENDING;
    }

    public TechRequestStatus getStatus() {
        return status;
    }

    public void setStatus(TechRequestStatus status) {
        FieldValidator.requireNonNull(status, "Status");
        this.status = status;
    }

    public Employee getEmployee() {
        return employee;
    }

    public TechSupportSpecialist getSpecialist() {
        return specialist;
    }



    public void setSpecialist(TechSupportSpecialist specialist) {
        FieldValidator.requireNonNull(specialist, "Specialist");
        this.specialist = specialist;
    }

    public String getContent() {
        return content;
    }


    @Override
    public String asLine() {
        return String.format("ID: %d | Status: %s | From: %s | To: %s",
                id, status, employee.getFullname(), specialist.getFullname());
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
