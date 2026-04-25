package services;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Dean;
import model.domain.DeletedUser;
import model.domain.Teacher;
import model.domain.TeacherComplaint;
import model.domain.User;
import model.factories.ServiceFactory;
import model.repository.ComplaintRepository;
import services.events.UserDeleteEvent;

public class ComplaintService extends BaseService<TeacherComplaint, ComplaintRepository>{

    private final UserService userService;

    public ComplaintService() {
        super(ComplaintRepository.getInstance());
        this.userService = ServiceFactory.getInstance().getService(UserService.class);
        subscribeToEvents();
    }

    public void sendComplaint(TeacherComplaint complaint) {
        User teacher = userService.get(complaint.getSenderId());
        User dean = userService.get(complaint.getReceiverId());
        User student = userService.get(complaint.getStudentId());
        if(teacher.getId() == DeletedUser.ID || dean.getId() == DeletedUser.ID){
            throw new OperationNotAllowed(" sending complaints to/from deleted account");
        }
        if(!teacher.getClass().equals(Teacher.class)){
            throw new OperationNotAllowed(" sending complaints from non-teacher account");
        }
        if(!dean.getClass().equals(Dean.class)){
            throw new OperationNotAllowed(" sending complaints to non-dean account");
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
        System.out.println("REGISTERED THE EVENT IN COMPLAINTS");
        eventSystem.subscribe(UserDeleteEvent.class, eventData -> {
                System.out.println("HIT THE EVENT IN COMPLAINTS");
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
