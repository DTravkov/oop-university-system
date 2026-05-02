package services;

import java.util.List;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.StudentOrganization;
import model.domain.User;
import model.repository.StudentOrganizationRepository;
import services.events.UserDeleteEvent;
import settings.AppSettings;

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
        return super.create(org);
    }

    public void addMember(int organizationId, int studentId) {
        StudentOrganization org = this.get(organizationId);
        User user = userService.get(studentId);
        if(!AppSettings.ST_ORG_ALLOWED_PRESIDENT_CLASSES.contains(user.getClass())){
            throw new OperationNotAllowed("adding not a student to a Student Organization members");
        }
        org.addMember(studentId);
        this.update(org);
    }

    public void removeMember(int organizationId, int studentId) {
        StudentOrganization org = this.get(organizationId);
        if(!org.getMembers().contains(studentId)){
            throw new DoesNotExist("no member with id : " + studentId + " in organization");
        }
        org.removeMember(studentId);
        this.update(org);
    }

    public void setPresident(int organizationId, int studentId) {
        StudentOrganization org = this.get(organizationId);
        User user = userService.get(studentId);
        if(!AppSettings.ST_ORG_ALLOWED_PRESIDENT_CLASSES.contains(user.getClass())){
            throw new OperationNotAllowed("adding not a student to a Student Organization members");
        }
        org.setPresidentId(studentId);
        org.addMember(studentId);
        this.update(org);
    }

    public void removePresident(int organizationId) {
        StudentOrganization org = this.get(organizationId);
        org.removeMember(org.getPresidentId());
        org.setPresidentId(AppSettings.DELETED_USER_ID);
        this.update(org);
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
                }
                else if(organization.getMembers().contains(deletedUserId)){
                    organization.removeMember(deletedUserId);
                    this.update(organization);
                }
            }

            

        });
    }

        
    



}
