package services;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Dean;
import model.domain.Teacher;
import model.domain.TeacherComplaint;
import model.domain.User;
import services.events.UserDeleteEvent;

public class ComplaintService extends BaseService<TeacherComplaint>{


    public ComplaintService(UserService userService) {
        super(TeacherComplaint.class);
        subscribeToEvents();
    }

    public void sendComplaint(TeacherComplaint complaint) {
        this.create(complaint);
    }

    public void closeComplaint(TeacherComplaint complaint, Dean dean) {
        if(complaint.getDean().getId() != dean.getId()){
            throw new OperationNotAllowed("closing other deans' complaints");
        }
        this.delete(complaint);
    }

    public List<TeacherComplaint> getTeacherComplaints(Teacher teacher) {
        return getAll().stream()
                       .filter(comp -> comp.getTeacher().getId() == teacher.getId())
                       .toList();
    }

    public List<TeacherComplaint> getDeanComplaints(Dean dean) {
        return getAll().stream()
                       .filter(comp -> comp.getDean().getId() == dean.getId())
                       .toList();
    }


    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, eventData -> onUserDelete(eventData.getUser()));

    }

    public void onUserDelete(User user) {
        if(user instanceof Dean || user instanceof Teacher){
            getAll().forEach(comp -> {
                if(comp.getTeacher().getId() == user.getId() || comp.getDean().getId() == user.getId()){
                    this.delete(comp);
                }
            });
        }
        
    }

}
