package services;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Dean;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TeacherComplaint;
import model.domain.User;
import model.repository.ComplaintRepository;
import services.events.UserDeleteEvent;
import settings.AppSettings;

public class ComplaintService extends BaseService<TeacherComplaint, ComplaintRepository> {

    private final UserService userService;

    public ComplaintService(UserService userService) {
        super(ComplaintRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }

    public void sendComplaint(TeacherComplaint complaint) {

        User from = userService.get(complaint.getSender().getId());
        User to = userService.get(complaint.getReceiver().getId());
        User about = userService.get(complaint.getStudent().getId());

        if (from.getId() == AppSettings.DELETED_USER_ID || to.getId() == AppSettings.DELETED_USER_ID) {
            throw new OperationNotAllowed(" sending complaints to/from deleted account");
        }
        if (!(from instanceof Teacher)) {
            throw new OperationNotAllowed(" sending complaints from " + from.getClass().getSimpleName() + " +account");
        }
        if (!(to instanceof Dean)) {
            throw new OperationNotAllowed(" sending complaints to " + to.getClass().getSimpleName() + " account");
        }
        if (!(about instanceof Student)) {
            throw new OperationNotAllowed(" sending complaints about person who is " + about.getClass().getSimpleName());
        }

        complaint.setSender(from);
        complaint.setReceiver(to);
        complaint.setStudent(about);

        this.create(complaint);
    }

    public List<TeacherComplaint> getAllByTeacherId(int teacherId) {
        return repository.findAllByTeacherId(teacherId);
    }

    public List<TeacherComplaint> getAllByDeanId(int deanId) {
        return repository.findAllByDeanId(deanId);
    }

    @Override
    public void subscribeToEvents() {
        eventSystem.subscribe(UserDeleteEvent.class, eventData -> {
            int deletedId = eventData.getUserId();
            List<TeacherComplaint> list = this.getAll();

            for (TeacherComplaint comp : list) {
                if (comp.getSender().getId() == deletedId) {
                    this.delete(comp.getId());
                    continue;
                }
                if (comp.getReceiver().getId() == deletedId) {
                    this.delete(comp.getId());
                    continue;
                }
                if (comp.getStudent().getId() == deletedId) {
                    this.delete(comp.getId());
                }
            }
        });
    }

}
