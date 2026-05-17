package services;

import java.util.List;
import java.util.function.Predicate;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.domain.Notification;
import model.domain.ResearchPaper;
import model.domain.ResearcherProfile;
import model.domain.SerializableModel;
import model.domain.User;
import model.repository.Repository;
import services.events.concrete.NotificationCreateEvent;
import services.events.concrete.UserCreateEvent;
import services.events.concrete.UserDeleteEvent;
import settings.AppSettings;
import utils.Logger;

/**
 * ResearchService is a concrete service. It implements logic for researcher profiles and research papers (two repositories),
 * including co-authors, references, citations, and automatically creates researcher profile for some user classses.
 */
public class ResearchService extends BaseService<SerializableModel> {

    private final Repository<ResearchPaper> paperRepository;
    private final Repository<ResearcherProfile> profileRepository;

    public ResearchService() {
        // null here because service manages 2 repositories manually.
        // not the best style, but i didt want to create another service for papers. 
        super(null);
        this.paperRepository = new Repository<>(ResearchPaper.class);
        this.profileRepository = new Repository<>(ResearcherProfile.class);
    }

    // RESEARCHERS
    public ResearcherProfile createProfile(User user) {
        if (isResearcher(user)) {
            throw new AlreadyExists("researcher profile for user id=" + user.getId());
        }

        ResearcherProfile profile = profileRepository.save(new ResearcherProfile(user));

        this.eventSystem.publish(new NotificationCreateEvent(
            new Notification("You are now a researcher.", user)
        ));
        return profile;
    }


    // PAPERS
    public ResearchPaper createPaper(ResearchPaper paper) {
        paperRepository.save(paper);
        Logger.log("Create paper (" + paper.getId() + ")");
        return paper;
    }

    public void deletePaper(ResearchPaper paper) {
        for (ResearcherProfile profile : paper.getResearchers()) {
            // will automatically clear researcher's references as well
            paper.removeResearcher(profile); 
        }
        paperRepository.delete(paper);
        profileRepository.saveAll();
        Logger.log("Delete paper (" + paper.getId() + ")");
    }

    public void addParticipant(ResearchPaper paper, ResearcherProfile profile) {
        paper.addResearcher(profile);
        paperRepository.save(paper);
        profileRepository.save(profile);
        this.eventSystem.publish(new NotificationCreateEvent(
            new Notification("You have been added as a researcher to paper with id: " + paper.getId(), profile.getUser())
        ));
    }

    public void removeParticipant(ResearchPaper paper, ResearcherProfile profile) {
        paper.removeResearcher(profile);
        paperRepository.save(paper);
        profileRepository.save(profile);
    }

    public void addReference(ResearchPaper paper, ResearchPaper reference) {
        paper.addReference(reference);
        paperRepository.save(paper);
    }

    public void citePaper(ResearchPaper paper) {
        paper.cite();
        paperRepository.save(paper);
    }

    public void incrementViews(ResearchPaper paper) {
        paper.incrementViews();
        paperRepository.save(paper);
    }

    // QUERIES

    public boolean isResearcher(User user) {
        return profileRepository.find(p -> p.getUser().equals(user)) != null;
    }

    public ResearcherProfile getProfile(User user) {
        ResearcherProfile match = profileRepository.find(p -> p.getUser().equals(user));
        if (match == null) {
            throw new DoesNotExist("researcher profile for user id=" + user.getId());
        }
        return match;
    }

    public List<ResearcherProfile> getAllProfiles(){
        return profileRepository.getAll();
    }

    public List<ResearcherProfile> getAllProfiles(Predicate<ResearcherProfile> query){
        return profileRepository.findAll(query);
    }



    public List<ResearchPaper> getAllPapers() {
        return paperRepository.getAll();
    }

    public List<ResearchPaper> getAllPapers(User user) {
        ResearcherProfile profile = getProfile(user);
        return paperRepository.findAll(p -> p.getResearchers().contains(profile));
    }

    public List<ResearchPaper> getAllPapers(Predicate<ResearchPaper> query) {
        return paperRepository.findAll(query);
    }



    // EVENT HANDLING

    @Override
    public void subscribeToEvents() {
        eventSystem.subscribe(UserCreateEvent.class, event -> {
                User createdUser = event.getUser();
                if (isResearcher(createdUser)) {
                    return;
                }
                if (AppSettings.DEFAULT_RESEARCHER_CLASSES.contains(createdUser.getClass())) {
                    createProfile(createdUser);
                }
        });

    }

}
