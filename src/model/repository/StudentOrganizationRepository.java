package model.repository;


import model.domain.StudentOrganization;


public class StudentOrganizationRepository extends Repository<StudentOrganization> {

    private static final StudentOrganizationRepository INSTANCE = new StudentOrganizationRepository();

    private StudentOrganizationRepository() {
        super();
    }

    public static StudentOrganizationRepository getInstance() {
        return INSTANCE;
    }

    public StudentOrganization findByPresidentId(int presidentId){
        return this.findFirst(org -> org.getPresidentId() == presidentId).orElse(null);
    }

    public StudentOrganization findByName(String name){
        return this.findFirst(org -> org.getName().equals(name)).orElse(null);
    }

    public boolean existsByName(String name){
        return this.findByName(name) != null;
    }

}
