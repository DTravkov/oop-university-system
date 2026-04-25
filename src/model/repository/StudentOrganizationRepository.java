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
        return this.data.values()
                        .stream()
                        .filter(organization -> organization.getPresidentId() == presidentId)
                        .findFirst()
                        .orElse(null);
    }

    public StudentOrganization findByName(String name){
        return this.data.values()
                        .stream()
                        .filter(organization -> organization.getName().equals(name))
                        .findFirst()
                        .orElse(null);
    }

    public boolean existsByName(String name){
        return findByName(name) != null;
    }

}
