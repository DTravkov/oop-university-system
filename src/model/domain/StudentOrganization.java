package model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import utils.FieldValidator;

public class StudentOrganization extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private int presidentId;
    private final List<Integer> members;

    public StudentOrganization(String name, String description, int presidentId) {
        FieldValidator.requireNonBlank(name, "Organization name");
        FieldValidator.requirePositive(presidentId, "Organization president id");

        this.name = name;
        this.description = description;
        this.presidentId = presidentId;
        this.members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPresidentId() {
        return presidentId;
    }

    public void setPresidentId(int presidentId) {
        this.presidentId = presidentId;
    }

    public List<Integer> getMembers() {
        return List.copyOf(members);
    }

    public void addMember(int studentId) {
        if (!this.members.contains(studentId)) {
            this.members.add(studentId);
        }
    }

    public void removeMember(int studentId) {
        this.members.remove(Integer.valueOf(studentId));
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StudentOrganization that = (StudentOrganization) o;
        if (this.id != 0 && that.getId() != 0) return this.id == that.getId();
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "StudentOrganization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", presidentId=" + presidentId +
                ", members=" + members +
                '}';
    }
}
