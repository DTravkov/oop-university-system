package model.domain;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TechRequest that = (TechRequest) o;
        if (id != 0 && that.getId() != 0) {
            return id == that.getId();
        }
        return status == that.status
                && Objects.equals(employee, that.employee)
                && Objects.equals(specialist, that.specialist)
                && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(status, employee, specialist, content);
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
