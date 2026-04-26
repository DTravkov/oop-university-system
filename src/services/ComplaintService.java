package services;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Dean;
import model.domain.DeletedUser;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TeacherComplaint;
import model.domain.User;
import model.repository.ComplaintRepository;
import services.events.UserDeleteEvent;

public class ComplaintService extends BaseService<TeacherComplaint, ComplaintRepository>{

    private final UserService userService;

    public ComplaintService(UserService userService) {
        super(ComplaintRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }

    public void sendComplaint(TeacherComplaint complaint) {

        User from = userService.get(complaint.getSenderId());
        User to = userService.get(complaint.getReceiverId());
        User about = userService.get(complaint.getStudentId());

        if(from.getId() == DeletedUser.ID || to.getId() == DeletedUser.ID){
            throw new OperationNotAllowed(" sending complaints to/from deleted account");
        }
        if(!(from instanceof Teacher)){
            throw new OperationNotAllowed(" sending complaints from " + from.getClass().getSimpleName() + " +account");
        }
        if(!(to instanceof Dean)){
            throw new OperationNotAllowed(" sending complaints to " + to.getClass().getSimpleName() + " account");
        }
        if(!(about instanceof Student)){
            throw new OperationNotAllowed(" sending complaints about person who is " + about.getClass().getSimpleName());
        }

        this.create(complaint);
    }

    public List<TeacherComplaint> getAllByTeacherId(int teacherId) {
        return repository.findAllByTeacherId(teacherId);
    }

    public List<TeacherComplaint> getAllByDeanId(int deanId) {
        return repository.findAllByDeanId(deanId);
    }

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, eventData -> {

                int deletedId = eventData.getUserId();
                List<TeacherComplaint> list = repository.findAll();
                
                for(TeacherComplaint comp : list){
                    if(comp.getSenderId() == deletedId){
                        this.delete(comp.getId());
                        continue;
                    }
                    if(comp.getReceiverId() == deletedId){
                        this.delete(comp.getId());
                        continue;
                    }
                    if(comp.getStudentId() == deletedId){
                        this.delete(comp.getId());
                        continue;
                    }
                }

        }
    );

    }

}
