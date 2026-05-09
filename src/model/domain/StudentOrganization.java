package model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import settings.AppSettings;
import utils.FieldValidator;

public class StudentOrganization extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private User president;
    private final List<User> members;

    public StudentOrganization(String name, String description, User president) {
        FieldValidator.requireNonBlank(name, "Organization name");
        FieldValidator.requireNonNull(president, "President");

        this.name = name;
        this.description = description;
        this.president = president;
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

    public User getPresident() {
        return president;
    }

    public void setPresident(User president) {
        FieldValidator.requireNonNull(president, "President");
        this.president = president;
    }

    public int getPresidentId() {
        return president.getId();
    }

    public void setPresidentId(int presidentId) {
        if (presidentId == AppSettings.DELETED_USER_ID) {
            this.president = new DeletedUser();
        } else {
            throw new IllegalArgumentException("Resolve via UserService and call setPresident(User)");
        }
    }

    public List<User> getMembers() {
        return List.copyOf(members);
    }

    public void addMember(User member) {
        FieldValidator.requireNonNull(member, "Member");
        if (!this.members.contains(member)) {
            this.members.add(member);
        }
    }

    public void removeMember(int memberId) {
        this.members.removeIf(u -> u.getId() == memberId);
    }

    public boolean removeMember(User member) {
        return members.remove(member);
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
                ", president=" + president +
                ", members=" + members +
                '}';
    }
}
