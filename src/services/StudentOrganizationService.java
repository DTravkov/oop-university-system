package services;

import java.util.List;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.DeletedUser;
import model.domain.Student;
import model.domain.StudentOrganization;
import model.domain.User;
import model.repository.StudentOrganizationRepository;
import services.events.UserDeleteEvent;

public class StudentOrganizationService extends BaseService<StudentOrganization, StudentOrganizationRepository>  {

    private final UserService userService;

    public StudentOrganizationService(UserService userService) {
        super(StudentOrganizationRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }

    @Override
    public StudentOrganization create(StudentOrganization org) {
        if(repository.existsByName(org.getName())){
            throw new AlreadyExists("organization with name : "+ org.getName());
        }
        userService.get(org.getPresidentId());
        org.addMember(org.getPresidentId());
        return repository.save(org);
    }

    public void addMember(int organizationId, int studentId) {
        StudentOrganization org = this.get(organizationId);
        User user = userService.get(studentId);
        if(!(user instanceof Student)){
            throw new OperationNotAllowed("adding not a student to a Student Organization members");
        }
        org.addMember(studentId);
        repository.save(org);
    }

    public void removeMember(int organizationId, int studentId) {
        StudentOrganization org = this.get(organizationId);
        if(!org.getMembers().contains(studentId)){
            throw new DoesNotExist("no member with id : " + studentId + " in organization");
        }
        org.removeMember(studentId);
        repository.save(org);
    }

    public void setPresident(int organizationId, int studentId) {
        StudentOrganization org = this.get(organizationId);
        User user = userService.get(studentId);
        if(!(user instanceof Student)){
            throw new OperationNotAllowed("adding not a student to a Student Organization members");
        }
        org.setPresidentId(studentId);
        org.addMember(studentId);
        repository.save(org);
    }

    public void removePresident(int organizationId) {
        StudentOrganization org = this.get(organizationId);
        org.removeMember(org.getPresidentId());
        org.setPresidentId(DeletedUser.ID);
        repository.save(org);
    }

    public StudentOrganization getByPresidentId(int presidentId){
        return repository.findByPresidentId(presidentId);
    }

    public StudentOrganization getByName(String name){
        StudentOrganization org = repository.findByName(name);
        if(org == null){
            throw new DoesNotExist("organization with name : " + name);
        }
        return org;
    }

    
    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {

            int deletedUserId = event.getUserId();
            List<StudentOrganization> list = repository.findAll();
            
            for(StudentOrganization organization : list){
                if(organization.getPresidentId() == deletedUserId){
                    this.removePresident(organization.getId());
                    repository.save(organization);
                }
                else if(organization.getMembers().contains(deletedUserId)){
                    organization.removeMember(deletedUserId);
                    repository.save(organization);
                }
            }

            

        });
    }

        
    



}
