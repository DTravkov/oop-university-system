package model.repository;

import java.util.List;
import java.util.Optional;

import model.domain.ResearchPaper;
import model.domain.ResearchProject;
import model.domain.ResearcherProfile;
import model.domain.SerializableModel;

public class ResearchRepository extends Repository<SerializableModel> {
    private static final ResearchRepository INSTANCE = new ResearchRepository();

    private ResearchRepository() {
        super();
    }

    public static ResearchRepository getInstance() {
        return INSTANCE;
    }

    public Optional<ResearcherProfile> findResearcherProfile(int userId) {
        return this.findAllByClass(ResearcherProfile.class)
                   .stream()
                   .map(entity -> (ResearcherProfile) entity)
                   .findFirst();
    }

    public boolean researcherProfileExists(int userId) {
        return this.findResearcherProfile(userId).isPresent();
    }

    public List<SerializableModel> findAllResearcherProfiles() {
        return this.findAllByClass(ResearcherProfile.class);
    }
    public List<SerializableModel> findAllResearchPapers() {
        return this.findAllByClass(ResearchPaper.class);
    }
    public List<SerializableModel> findAllResearchProjects() {
        return this.findAllByClass(ResearchProject.class);
    }
    
    private List<SerializableModel> findAllByClass(Class<? extends SerializableModel> dotClass) {
        return super.findAll((entity) -> entity.getClass().equals(dotClass));
    }

    private List<SerializableModel> findAllByClassOrSubclass(Class<? extends SerializableModel> dotClass) {
        return super.findAll((entity) -> dotClass.isAssignableFrom(entity.getClass()));
    }

}
