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

        User teacher = userService.get(complaint.getSenderId());
        User dean = userService.get(complaint.getReceiverId());
        User student = userService.get(complaint.getStudentId());

        if(teacher.getId() == DeletedUser.ID || dean.getId() == DeletedUser.ID){
            throw new OperationNotAllowed(" sending complaints to/from deleted account");
        }
        if(!(teacher instanceof Teacher)){
            throw new OperationNotAllowed(" sending complaints from non-teacher account");
        }
        if(!(dean instanceof Dean)){
            throw new OperationNotAllowed(" sending complaints to non-dean account");
        }
        if(!(student instanceof Student)){
            throw new OperationNotAllowed(" sending complaints about person who is not student");
        }

        repository.save(complaint);
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
