package services;

import java.util.List;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;

import model.domain.StudentOrganization;
import model.domain.User;
import model.dto.StudentOrganizationDTO;
import model.dto.UserDTO;
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
        if(getByPresidentId(org.getPresidentId()) != null){
            throw new AlreadyExists("organization led by student " + org.getPresidentId()); 
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
            throw new DoesNotExist("member with id : " + studentId + " in organization");
        }
        if(org.getPresidentId() == studentId){
            throw new OperationNotAllowed("removing president from organization member list");
        }
        org.removeMember(studentId);
        this.update(org);
    }

    public void setPresident(int organizationId, int studentId) {
        StudentOrganization org = this.get(organizationId);
        User user = userService.get(studentId);
        if(!AppSettings.ST_ORG_ALLOWED_PRESIDENT_CLASSES.contains(user.getClass())){
            throw new OperationNotAllowed("adding not allowed role as a Student Organization president");
        }
        if(org.getPresidentId() == studentId){
            throw new AlreadyExists(" president of this organization with the same id");
        }
        if(getByPresidentId(studentId) != null){
            throw new AlreadyExists(" this student as a president of other organization");
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

    public StudentOrganizationDTO getDTO(int organizationId) {
        StudentOrganization organization = get(organizationId);
        return getDTO(organization);
    }

    public StudentOrganizationDTO getDTO(StudentOrganization organization) {
        UserDTO president = userService.getDTO(organization.getPresidentId());
        List<UserDTO> memberDtos = organization.getMembers().stream()
                .map(userService::getDTO)
                .toList();
        return new StudentOrganizationDTO(organization, president, memberDtos);
    }

    
    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            cleanUpOrganizationData(event.getUserId());
        });
    }

    public void cleanUpOrganizationData(int deletedUserId) {
        List<StudentOrganization> list = repository.findAll();
        for (StudentOrganization organization : list) {
            if (organization.getPresidentId() == deletedUserId) {
                organization.removeMember(deletedUserId);
                organization.setPresidentId(AppSettings.DELETED_USER_ID);
            } else if (organization.getMembers().contains(deletedUserId)) {
                organization.removeMember(deletedUserId);
            }
        }
        this.saveAll();
    }

        
    



}
