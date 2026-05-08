package services;

import java.util.ArrayList;
import java.util.List;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.GraduateStudent;
import model.domain.ResearchPaper;
import model.domain.ResearchProject;
import model.domain.ResearcherProfile;
import model.domain.SerializableModel;
import model.domain.User;
import model.dto.ResearchPaperDTO;
import model.dto.ResearchProjectDTO;
import model.dto.ResearcherProfileDTO;
import model.dto.UserDTO;
import model.repository.ResearchRepository;
import services.events.UserCreateEvent;
import services.events.UserDeleteEvent;
import settings.AppSettings;
import utils.Comparators;

public class ResearchService extends BaseService<SerializableModel, ResearchRepository> {

    private final UserService userService;

    public ResearchService(UserService userService) {
        super(ResearchRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }

    public ResearchPaper createPaper(ResearchPaper paper) {
        return (ResearchPaper) super.create(paper);
    }

    public ResearchProject createProject(ResearchProject project) {
        return (ResearchProject) super.create(project);
    }

    public void deleteProject(int projectId) {
        getProject(projectId);
        cleanUpProjectData(projectId);
        super.delete(projectId);
    }

    public void deletePaper(int paperId) {
        getPaper(paperId);
        super.delete(paperId);
    }

    public ResearcherProfile createResearcher(int userId) {
        userService.get(userId);

        if(repository.isResearcher(userId))
            throw new AlreadyExists("researcher profile for user with id=" + userId);

        return (ResearcherProfile) super.create(new ResearcherProfile(userId));
    }

    public void deleteResearcher(int userId) {
        ResearcherProfile profileToDelete = getResearcher(userId);
        super.delete(profileToDelete);
    }

    public void joinProject(int projectId, int userId){
        ResearchProject project = getProject(projectId);
        ResearcherProfile profile = getResearcher(userId);
        if(project.getParticipants().contains(userId)){
            throw new AlreadyExists(" researcher with id="+userId + " in project with id=" + projectId);
        }
        project.addParticipant(userId);
        profile.addResearchProject(projectId);
        super.update(profile);
        super.update(project);
    }

    public void removeFromProject(int projectId, int researcherId){
        ResearchProject project = getProject(projectId);
        ResearcherProfile profile = getResearcher(researcherId);
        if(!project.getParticipants().contains(researcherId)){
            throw new DoesNotExist(" researcher with id="+researcherId + "in project with id=" + projectId);
        }
        project.removeParticipant(researcherId);
        profile.removeResearchProject(projectId);
        super.update(profile);
        super.update(project);
    }

    public void addParticipantToProject(int projectId, int researcherId){
        joinProject(projectId, researcherId);
    }

    public void removeParticipantFromProject(int projectId, int researcherId){
        removeFromProject(projectId, researcherId);
    }

    public void addParticipantToPaper(int paperId, int researcherId){
        ResearchPaper paper = getPaper(paperId);
        getResearcher(researcherId);
        if(paper.getParticipants().contains(researcherId)){
            throw new AlreadyExists(" researcher with id="+researcherId + " in paper with id=" + paperId);
        }
        paper.addParticipant(researcherId);
        super.update(paper);
    }

    public void removeParticipantFromPaper(int paperId, int researcherId){
        ResearchPaper paper = getPaper(paperId);
        getResearcher(researcherId);
        if(!paper.getParticipants().contains(researcherId)){
            throw new DoesNotExist(" researcher with id="+researcherId + " in paper with id=" + paperId);
        }
        paper.removeParticipant(researcherId);
        super.update(paper);
    }

    public int calculateHIndex(int userId) {
        List<ResearchPaper> papers = new ArrayList<>(getResearcherPapers(userId));
        papers.sort(Comparators.RESEARCH_PAPER_BY_CITATIONS_DESC);
        
        int hIndex = 0;
        for (int i = 0; i <papers.size();i++) {
            if (papers.get(i).getCitations() >= i + 1) {
                hIndex = i + 1;
            } else {
                break;
            }
        }
        return hIndex;
    }

    public void assignSupervisor(int gradStudentId, int supervisorId){
        User user = userService.get(gradStudentId);
        if (user.getId() == supervisorId){
            throw new OperationNotAllowed("assigning a user as his own supervisor");
        }
        if(!(user instanceof GraduateStudent castedGraduateStudent)){
            throw new OperationNotAllowed("assignment of supervisor to " + user.getClass().getSimpleName());
        }
        if(calculateHIndex(supervisorId) < 3){
            throw new OperationNotAllowed("assginment of supervisor whose h-index is lower than 3 (" + calculateHIndex(supervisorId) +")");
        }
        
        castedGraduateStudent.setSupervisorId(supervisorId);
        userService.update(castedGraduateStudent);
    }

    public void removeSupervisor(int gradStudentId){
        User user = userService.get(gradStudentId);
        if(!(user instanceof GraduateStudent castedGraduateStudent)){
            throw new OperationNotAllowed("removal of supervisor from " + user.getClass().getSimpleName());
        }
        castedGraduateStudent.setSupervisorId(AppSettings.DELETED_USER_ID);
        userService.update(castedGraduateStudent);
    }

    
    
    public ResearchProject getProject(int projectId){
        return repository.findProject(projectId)
                         .orElseThrow(()->new DoesNotExist("project with id=" + projectId));
    }

    public List<ResearchPaper> getResearcherPapers(int userId){
        return getAllPapers().stream().filter(p -> p.getParticipants().contains(userId)).toList();
    }

    public List<ResearchProject> getResearcherProjects(int userId){
        ResearcherProfile profile = getResearcher(userId);
        return profile.getResearchProjects().stream().map(id -> getProject(id)).toList();
    }

    public ResearchPaper getPaper(int paperId){
        return repository.findPaper(paperId)
                         .orElseThrow(()->new DoesNotExist("paper with id=" + paperId));
    }

    public List<ResearchProject> getAllProjects() {
        return repository.findAllProjects();
    }

    public List<ResearchPaper> getAllPapers() {
        return repository.findAllPapers();
    }

    public ResearcherProfile getResearcher(int userId){
        return repository.findResearcher(userId)
                         .orElseThrow(()->new DoesNotExist("researcher with userId=" + userId));
    }

    public List<ResearcherProfile> getAllResearchers(){
        return repository.findAllResearchers();
    }

    public User getSupervisorByStudentId(int gradStudentId){
        User gradStudent = userService.get(gradStudentId);
        if(!(gradStudent instanceof GraduateStudent castedGraduateStudent)){
            throw new OperationNotAllowed("search supervisor of a non-graduate-student user with id=" + gradStudentId);
        }
        return userService.get(castedGraduateStudent.getSupervisorId());
    }

    public List<GraduateStudent> getStudentsBySupervisorId(int supervisorId){
        return userService.getAllByClassOrSubclass(GraduateStudent.class)
                          .stream()
                          .map(st -> (GraduateStudent) st)
                          .filter(st -> ((GraduateStudent) st).getSupervisorId() == supervisorId)
                          .toList();
    }

    // DTOs
    public ResearchProjectDTO getProjectDTO(int projectId) {
        ResearchProject project = repository.findProject(projectId)
                                            .orElseThrow(() -> new DoesNotExist("project with id=" + projectId));
        List<UserDTO> participantDTOs = project.getParticipants().stream().map(userService::getDTO).toList();
        return new ResearchProjectDTO(project, participantDTOs);
    }

    public ResearchPaperDTO getPaperDTO(int paperId) {
        ResearchPaper paper = repository.findPaper(paperId)
                                        .orElseThrow(() -> new DoesNotExist("paper with id=" + paperId));
        List<UserDTO> participantDTOs = paper.getParticipants().stream().map(userService::getDTO).toList();
        return new ResearchPaperDTO(paper, participantDTOs);
    }

    public ResearcherProfileDTO getResearcherDTO(int userId) {
        ResearcherProfile profile = repository.findResearcher(userId)
                                              .orElseThrow(() -> new DoesNotExist("researcher with userId=" + userId));
        UserDTO userDTO = userService.getDTO(profile.getUserId());
        List<ResearchProjectDTO> projectDTOs = profile.getResearchProjects()
                                                      .stream()
                                                      .map(this::getProjectDTO)
                                                      .toList();
        return new ResearcherProfileDTO(profile, userDTO, projectDTOs);
    }




    public boolean isResearcher(int userId) {
        return repository.isResearcher(userId);
    }

    public void cleanUpResearcherData(int userId){
        var papers = repository.findAllPapers().stream().filter(paper -> paper.getParticipants().contains(userId)).toList();
        var projects = repository.findAllProjects().stream().filter(project -> project.getParticipants().contains(userId)).toList();
        var students = getStudentsBySupervisorId(userId);
        papers.forEach(paper -> paper.removeParticipant(userId));
        projects.forEach(project -> project.removeParticipant(userId));
        students.forEach(gradStudent -> gradStudent.removeSupervisor());

        userService.saveAll();
        super.saveAll();
    }

    public void cleanUpProjectData(int projectId){
        var researchers = repository.findAllResearchers().stream().filter(researcher -> researcher.getResearchProjects().contains(projectId)).toList();
        researchers.forEach(researcher -> researcher.removeResearchProject(projectId));
        super.saveAll();
    }

    @Override
    public void subscribeToEvents() {
        //creates researcher profile when :
        // 1) when user is registered 
        // 2) his class belongs to DEFAULT_RESEARCHER_CLASS in src/settings/AppSettings.java
        eventSystem.subscribe(UserCreateEvent.class, (eventData) -> {

            int createdUserId = eventData.getUserId();
            
            if(isResearcher(createdUserId)) 
                return;

            Class<? extends User> userClass = eventData.getUserClass();

            if(AppSettings.DEFAULT_RESEARCHER_CLASSES.contains(userClass))
                createResearcher(eventData.getUserId());

        });

        //deletes researcher profile, his membership in projects, membership in papers, and supervisor record from his students when :
        // a user being researcher is deleted from system.
        eventSystem.subscribe(UserDeleteEvent.class, (eventData) -> {

            int deletedUserId = eventData.getUserId();
            if(isResearcher(deletedUserId)){
                cleanUpResearcherData(deletedUserId);
                deleteResearcher(deletedUserId);
            }

        });
    }


}
