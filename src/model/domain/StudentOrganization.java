package model.domain;

import java.util.ArrayList;
import java.util.List;

import exceptions.AlreadyExists;
import exceptions.OperationNotAllowed;
import settings.AppSettings;
import utils.FieldValidator;

public class StudentOrganization extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Student president;
    private final List<Student> members;

    public StudentOrganization(String name, String description, Student president) {
        FieldValidator.requireNonBlank(name, "Organization name");
        FieldValidator.requireNonNull(president, "President");

        this.name = name;
        this.description = description;
        this.president = president;
        this.members = new ArrayList<>();
        this.members.add(president);

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

    public void setPresident(Student president) {
        FieldValidator.requireNonNull(president, "President");
        if(this.president.getId() == president.getId()){
            throw new AlreadyExists("this user os already a president");
        }
        this.president = president;
        this.addMember(president);
    }

    public void removePresident() {
        this.president = AppSettings.DELETED_STUDENT;
        this.removeMember(president);
    }


    public List<Student> getMembers() {
        return List.copyOf(members);
    }

    public void addMember(Student member) {
        FieldValidator.requireNonNull(member, "Member");
        if(this.members.contains(member))
            throw new AlreadyExists("member of " + getName() + " organization with id=" + member.getId());
        this.members.add(member);
    }

    public boolean removeMember(Student member) {
        if(member.equals(president)){
            throw new OperationNotAllowed("deleting actual president");
        }
        return members.remove(member);
    }



    @Override
    public String asLine() {
        return String.format("ID: %d | Name: %s | Members: %d | President: %s",
                id, name, members.size(), president.getFullname());
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append('\n');
        sb.append("Name: ").append(name).append('\n');
        sb.append("Description:\n").append(description).append('\n');
        sb.append("/President/\n").append(president.asLine()).append('\n');
        sb.append("/Members/\n");
        for (Student m : members) {
            sb.append(m.asLine()).append('\n');
        }
        return sb.toString();
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
