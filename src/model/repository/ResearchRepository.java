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

    public List<ResearchPaper> findAllPapers() {
        return this.findAllByClass(ResearchPaper.class)
                   .stream()
                   .map(entity -> (ResearchPaper) entity)
                   .toList();
    }

    public List<ResearchProject> findAllProjects() {
        return this.findAllByClass(ResearchProject.class)
                   .stream()
                   .map(entity -> (ResearchProject) entity)
                   .toList();
    }

    public List<ResearcherProfile> findAllResearchers() {
        return this.findAllByClass(ResearcherProfile.class)
                   .stream()
                   .map(entity -> (ResearcherProfile) entity)
                   .toList();
    }
    

    public Optional<ResearcherProfile> findResearcher(int userId) {
        return this.findAllResearchers().stream()
                                        .filter(researcher -> researcher.getUserId() == userId)
                                        .findFirst();
    }

    public Optional<ResearchProject> findProject(int projectId) {
        return this.findAllProjects().stream()
                                     .filter(project -> project.getId() == projectId)
                                     .findFirst();
    }

    public Optional<ResearchPaper> findPaper(int paperId) {
        return this.findAllPapers().stream()
                                   .filter(paper -> paper.getId() == paperId)
                                   .findFirst();
    }

    public boolean isResearcher(int userId){
        return this.findAllResearchers().stream()
                                        .filter(researcher -> researcher.getUserId() == userId)
                                        .findFirst()
                                        .isPresent();
    }

}
