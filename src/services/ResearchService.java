package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.domain.GraduateStudent;
import model.domain.ResearchPaper;
import model.domain.ResearchProject;
import model.domain.ResearcherProfile;
import model.domain.SerializableModel;
import model.domain.Teacher;
import model.domain.User;
import model.repository.ResearchRepository;
import services.events.UserCreateEvent;
import services.events.UserDeleteEvent;

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

    public ResearcherProfile createResearcherProfile(int userId) {

        userService.get(userId);

        if(repository.researcherProfileExists(userId))
            throw new AlreadyExists("researcher profile for user with id : " + userId);

        return (ResearcherProfile) super.create(new ResearcherProfile(userId));
    }

    public ResearcherProfile getResearcherProfile(int userId) {
        ResearcherProfile profile = repository.findResearcherProfile(userId)
                                              .orElseThrow(()->new DoesNotExist("researcher profile for user with id="+userId));
        return profile;
    }

    public void deleteResearcerProfile(int userId) {
        ResearcherProfile profileToDelete = getResearcherProfile(userId);
        super.delete(profileToDelete.getId());
    }


    @Override
    public void subscribeToEvents() {

        eventSystem.subscribe(UserCreateEvent.class, (eventData) -> {

            int createdUserId = eventData.getUserId();
            
            if(repository.researcherProfileExists(createdUserId)) return;

            Class<? extends User> userClass = eventData.getUserClass();

            if(userClass.equals(GraduateStudent.class) || userClass.equals(Teacher.class)){
                this.createResearcherProfile(eventData.getUserId());
            }

        });

        eventSystem.subscribe(UserDeleteEvent.class, (eventData) -> {

            int deletedUserId = eventData.getUserId();
            if(repository.researcherProfileExists(deletedUserId)){

            }
        });
    }


}
