package model.repository;

import model.domain.Employee;
import model.domain.Message;


public class MessageRepository extends Repository<Message> {

    public MessageRepository() {
        super("messages.ser");
    }

    public Message getEmployeeSentMessages(Employee emp){
        return this.getEmployeeSentMessages(emp.getId());
    }

    public Message getEmployeeSentMessages(int employeeId){
        return this.data.values().stream()
                .filter(entity -> entity.getSender().getId() == employeeId)
                .findFirst()
                .orElse(null);
    }

    public Message getEmployeeReceivedMessages(Employee emp){
        return this.getEmployeeReceivedMessages(emp.getId());
    }

    public Message getEmployeeReceivedMessages(int employeeId){
        return this.data.values().stream()
                .filter(entity -> entity.getReceiver().getId() == employeeId)
                .findFirst()
                .orElse(null);
    }
}
