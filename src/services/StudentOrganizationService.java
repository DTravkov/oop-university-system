package services;


import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.domain.Student;
import model.domain.StudentOrganization;
import model.domain.User;
import services.events.concrete.UserDeleteEvent;
import utils.Logger;

/**
 * StudentOrganizationService is a concrete service. It implements logic for student clubs: president and members,
 * uniqueness rules so one student does not sit in two orgs in conflicting roles, and cleanup when a student is deleted.
 */
public class StudentOrganizationService extends GenericService<StudentOrganization>  {


    public StudentOrganizationService() {
        super(StudentOrganization.class);
    }

    // CREATE / UPDATE / DELETE

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
        Logger.log("Make student (" + student.getId() + ") a president of (" + org.getId() + ")");
        org.setPresident(student);
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



    // QUERIES

    public StudentOrganization getOrganizationByMember(Student member){
        StudentOrganization match = find(org -> org.getMembers().contains(member));
        if(match == null) throw new DoesNotExist("membership in any organization for student");
        return match;
    }

    public StudentOrganization getOrganizationByPresident(Student president){
        StudentOrganization match = find(org -> org.getPresident().equals(president));
        if(match == null) throw new DoesNotExist("presidentship in any organization for student");
        return match;
    }

    public boolean isPresident(Student student){
        StudentOrganization match = find(org -> org.getPresident().equals(student));
        return match != null;
    }
    
    public boolean isMember(Student student){
        StudentOrganization match = find(org -> org.getMembers().contains(student));
        return match != null;
    }

    // EVENT HANDLING

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> onUserDelete(event.getUser()));
    }


    public void onUserDelete(User deletedUser) {
        if(deletedUser instanceof Student student){
            StudentOrganization org = find(o -> o.getMembers().contains(student));
            if (org == null) return;
            if(!org.getPresident().equals(student)){
                org.removeMember(student); // i dont remove president here because president place cant be null
                update(org);
            }

        }
    }

}
