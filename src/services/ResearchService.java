package services;

import java.util.List;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.domain.ResearchPaper;
import model.domain.ResearchProject;
import model.domain.ResearcherProfile;
import model.domain.SerializableModel;
import model.domain.User;
import model.dto.ResearchPaperDTO;
import model.dto.ResearchProjectDTO;
import model.dto.ResearcherProfileDTO;
import model.repository.ResearchRepository;
import services.events.UserCreateEvent;
import services.events.UserDeleteEvent;
import settings.AppSettings;

public class ResearchService extends BaseService<SerializableModel, ResearchRepository> {

    private final UserService userService;

    public ResearchService(UserService userService) {
        super(ResearchRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }

    public SerializableModel createPaper(ResearchPaper paper) {
        return super.create(paper);
    }

    public SerializableModel createProject(ResearchProject project) {
        return super.create(project);
    }

    public ResearcherProfile makeResearcher(int userId) {
        return createResearcherProfile(userId);
    }

    public ResearcherProfile createResearcherProfile(int userId) {
        userService.get(userId);

        if(repository.researcherProfileExists(userId))
            throw new AlreadyExists("researcher profile for user with id : " + userId);

        return (ResearcherProfile) super.create(new ResearcherProfile(userId));
    }

    public void deleteResearcherProfile(int userId) {
        ResearcherProfile profileToDelete = getResearcherProfile(userId);
        super.delete(profileToDelete.getId());
    }


    public ResearcherProfile getResearcherProfile(int userId) {
        return repository.findResearcherProfile(userId)
                         .orElseThrow(() -> new DoesNotExist("researcher profile for user with id=" + userId));
    }

    public List<ResearcherProfile> getAllResearcherProfiles() {
        return repository.findAllResearcherProfiles();
    }

    public List<User> getAllResearchersBasicAccounts() {
        return repository.findAllResearcherUserIds()
                         .stream()
                         .map(userService::get)
                         .toList();
    }

    public boolean isResearcher(int userId) {
        return repository.researcherProfileExists(userId);
    }

    public ResearchProjectDTO getProjectDTO(int id) {
        SerializableModel model = get(id);
        if (!(model instanceof ResearchProject project)) {
            throw new DoesNotExist("ResearchProject record with id=" + id);
        }
        return getDTO(project);
    }

    public ResearchProjectDTO getDTO(ResearchProject project) {
        return new ResearchProjectDTO(project);
    }

    public ResearchPaperDTO getPaperDTO(int id) {
        SerializableModel model = get(id);
        if (!(model instanceof ResearchPaper paper)) {
            throw new DoesNotExist("ResearchPaper record with id=" + id);
        }
        return getDTO(paper);
    }

    public ResearchPaperDTO getDTO(ResearchPaper paper) {
        return new ResearchPaperDTO(paper);
    }

    public ResearcherProfileDTO getResearcherProfileDTO(int userId) {
        ResearcherProfile profile = getResearcherProfile(userId);
        return getDTO(profile);
    }

    public ResearcherProfileDTO getDTO(ResearcherProfile profile) {
        User user = userService.get(profile.getUserId());
        return new ResearcherProfileDTO(profile, user);
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
                this.createResearcherProfile(eventData.getUserId());
            

        });

        //deletes researcher profile when :
        // a user with researcher profile is deleted from a system
        eventSystem.subscribe(UserDeleteEvent.class, (eventData) -> {

            int deletedUserId = eventData.getUserId();

            if(isResearcher(deletedUserId))
                deleteResearcherProfile(deletedUserId);
            
        });
    }


}
