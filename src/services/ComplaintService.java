package services;

import java.util.ArrayList;
import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Dean;
import model.domain.DeletedUser;
import model.domain.Teacher;
import model.domain.TeacherComplaint;
import model.domain.User;
import model.repository.ComplaintRepository;
import services.events.Event;
import services.events.UserDeletedEvent;

public class ComplaintService extends BaseService<TeacherComplaint, ComplaintRepository>{

    private final UserService userService;

    public ComplaintService() {
        super(ComplaintRepository.getInstance());
        this.userService = new UserService();
    }

    public void sendComplaint(TeacherComplaint complaint) {
        User teacher = userService.get(complaint.getSenderId());
        User dean = userService.get(complaint.getReceiverId());
        if(teacher.getId() == DeletedUser.ID || dean.getId() == DeletedUser.ID){
            throw new OperationNotAllowed(" sending complaints to/from deleted account");
        }
        if(!teacher.getClass().equals(Teacher.class)){
            throw new OperationNotAllowed(" sending complaints from non-teacher account");
        }
        if(!dean.getClass().equals(Dean.class)){
            throw new OperationNotAllowed(" sending complaints to non-dean account");
        }
        complaint.setSenderId(teacher.getId());
        complaint.setReceiverId(dean.getId());
        repository.save(complaint);
    }

    public List<TeacherComplaint> getAllByTeacherId(int teacherId) {
        User teacher = userService.get(teacherId);
        List<TeacherComplaint> complaints = new ArrayList<>(repository.findAllByTeacherId(teacher.getId()));
        return complaints;
    }

    public List<TeacherComplaint> getAllByDeanId(int deanId) {
        User dean = userService.get(deanId);
        List<TeacherComplaint> complaints = new ArrayList<>(repository.findAllByDeanId(dean.getId()));
        return complaints;
    }

    @Override
    public void update(Event e) {
        if(e instanceof UserDeletedEvent event){
            for(TeacherComplaint c: repository.findAllByDeanId(event.getUserId())){
                c.setSenderId(DeletedUser.ID);
                repository.save(c);
            }
        }
    }
}
