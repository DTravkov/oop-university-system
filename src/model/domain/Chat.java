package model.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exceptions.OperationNotAllowed;
import utils.FieldValidator;

public class Chat extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private final Set<Employee> members = new HashSet<>(); 
    private List<Message> messages = new ArrayList<>();

    public Chat(Employee memberOne, Employee memberTwo) {
        FieldValidator.requireNonNull(memberOne, "Member one");
        FieldValidator.requireNonNull(memberTwo, "Member two");
        if (memberOne.equals(memberTwo)) {
            throw new OperationNotAllowed("c hat members must be distinct users");
        }
        this.members.add(memberOne);
        this.members.add(memberTwo);
    }


    public String getTitleFor(Employee employee){
        return getMembers().stream()
                    .filter(member -> member.getId() != employee.getId())
                    .map(member -> ((Employee) member)
                    .getFullname())
                    .toList().toString();
    }

    public Employee getOtherMember(Employee member) {
        for(Employee emp : members){
            if(emp.getId() != member.getId()){
                return emp;
            }
        }
        return null;
    }

    public Set<Employee> getMembers() {
        return members;
    }

    public boolean isMember(Employee employee) {
        return members.contains(employee);
    }

    public List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public void sendMessage(Message message) {
        FieldValidator.requireNonNull(message, "Message");
        if(messages.contains(message)){
            throw new OperationNotAllowed("adding a duplicate message to chat");
        }
        if(!isMember(message.getSender())){
            throw new OperationNotAllowed("sending message to inappropriate chat");
        }
        this.messages.add(message);
    }

    public boolean removeMessage(int messageId) {
        return messages.removeIf(m -> m.getId() == messageId);
    }

    public boolean removeMessage(Message message) {
        FieldValidator.requireNonNull(message, "Message");
        return messages.remove(message);
    }

    @Override
    public String asLine() {
        return String.format("ID: %d | Members: %d | Messages: %d", id, members.size(), messages.size());
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append('\n');
        sb.append("/Members/\n");
        for (Employee e : members) {
            sb.append(e.asLine()).append('\n');
        }
        sb.append("/Messages/\n");
        for (Message m : messages) {
            sb.append(m.asLine()).append('\n');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }
}
