package services;


import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.domain.Student;
import model.domain.StudentOrganization;
import model.domain.User;
import services.events.UserDeleteEvent;
import settings.AppSettings;
import utils.Logger;

public class StudentOrganizationService extends BaseService<StudentOrganization>  {


    public StudentOrganizationService() {
        super(StudentOrganization.class);
        subscribeToEvents();
    }

    @Override
    public StudentOrganization create(StudentOrganization org){
        if(isPresident(org.getPresident()) || isMember(org.getPresident())){
            throw new AlreadyExists("nominated person's presidentship/membership in other organization");
        }
        return super.create(org);
    }

    public void makePresident(StudentOrganization org, Student student){
        if(isPresident(student)){
            throw new AlreadyExists("student presidentship in other organization");
        }
        org.setPresident(student);
        update(org);
    }

    public void removePresident(StudentOrganization org){
        org.removePresident();
        update(org);
    }

    public void addMember(StudentOrganization org, Student student){
        if(isMember(student)){
            throw new AlreadyExists("student membership in other organization");
        }
        org.addMember(student);
        Logger.log("Add member (" + student.asLine() + ") to organization (" + org.getId() + ")");
        update(org);
    }

    public void removeMember(StudentOrganization org, Student student){
        if(!org.removeMember(student)){
            throw new DoesNotExist("student membership in " + org.getName());
        }
        Logger.log("Remove member (" + student.asLine() + ") from organization (" + org.getId() + ")");
        update(org);
    }



    public StudentOrganization getOrganizationByMember(User member){
        StudentOrganization match = find(org -> org.getMembers().contains(member));
        if(match == null) throw new DoesNotExist("membership in any organization for student");
        return match;
    }

    public StudentOrganization getOrganizationByPresident(User president){
        StudentOrganization match = find(org -> org.getPresident().equals(president));
        if(match != null) throw new DoesNotExist("presidentship in any organization for student");
        return match;
    }

    public boolean isPresident(User user){
        StudentOrganization match = find(org -> org.getPresident().equals(user));
        return match != null;
    }

    public boolean isMember(User user){
        StudentOrganization match = find(org -> org.getMembers().contains(user));
        return match != null;
    }

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            cleanUpOrganizationData(event.getUser());
        });
    }

    public void cleanUpOrganizationData(User deletedUser) {
        if(AppSettings.ST_ORG_ALLOWED_PRESIDENT_CLASSES.contains(deletedUser.getClass())){
            getAll().forEach(org -> {
                if(org.getPresident().getId() == deletedUser.getId()){
                    org.removePresident();
                }
            });
        }
    }

        
    



}
