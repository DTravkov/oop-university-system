package services;

import java.util.List;

import model.domain.Dean;
import model.domain.Teacher;
import model.domain.TeacherComplaint;
import model.domain.User;
import services.events.concrete.UserDeleteEvent;
import utils.Logger;

/**
 * ComplaintService is a concrete service. It implements logic for managing teacher complaints addressed to deans.
 */
public class ComplaintService extends GenericService<TeacherComplaint>{


    public ComplaintService() {
        super(TeacherComplaint.class);
    }

    // CREATE / UPDATE / DELETE

    public void closeComplaint(TeacherComplaint complaint, Dean dean) {
        complaint.closeBy(dean);
        Logger.log("Closed complaint (" + complaint.getId() + ")" );
        repository.save(complaint);
    }


    // QUERIES

    public List<TeacherComplaint> getComplaintsByTeacher(Teacher teacher) {
        return getAll(comp -> comp.getTeacher().getId() == teacher.getId());
    }

    public List<TeacherComplaint> getComplaintsByDean(Dean dean) {
        return getAll(comp -> comp.getDean().getId() == dean.getId());
    }


    // EVENT HANDLING

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, eventData -> onUserDelete(eventData.getUser()));

    }

    public void onUserDelete(User user) {
        if(user instanceof Dean || user instanceof Teacher){
            getAll().forEach(comp -> {
                if(comp.getTeacher().equals(user) || comp.getDean().equals(user)){
                    this.delete(comp);
                }
            });
        }
        
    }

}
