package services;

import java.util.List;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.ResearchPaper;
import model.domain.ResearcherProfile;
import model.domain.User;
import model.repository.Repository;
import services.events.UserCreateEvent;
import services.events.UserDeleteEvent;
import settings.AppSettings;
import utils.Logger;

public class ResearchService extends BaseService<ResearcherProfile> {

    private final Repository<ResearchPaper> paperRepository;

    public ResearchService() {
        super(ResearcherProfile.class);
        this.paperRepository = new Repository<>(ResearchPaper.class);
        subscribeToEvents();
    }


    public ResearcherProfile createResearcherProfile(User user) {
        if (isResearcher(user)) {
            throw new AlreadyExists("researcher profile for user id=" + user.getId());
        }
        return create(new ResearcherProfile(user));
    }


    public ResearchPaper createPaper(User author, String title) {
        ResearcherProfile profile = getProfile(author);
        ResearchPaper paper = paperRepository.save(new ResearchPaper(title));
        paper.addResearcher(profile);
        profile.addPaper(paper);
        paperRepository.save(paper);
        update(profile);
        Logger.log("Create paper (" + paper.getId() + ") by author (" + author.asLine() + ")");
        return paper;
    }

    public void deletePaper(ResearchPaper paper) {
        for (ResearcherProfile profile : paper.getResearchers()) {
            profile.removePaper(paper.getId());
            update(profile);
        }
        paperRepository.delete(paper);
        Logger.log("Delete paper (" + paper.getId() + ")");
    }

    public void addParticipant(ResearchPaper paper, ResearcherProfile profile) {
        if (paper.getResearchers().stream().anyMatch(r -> r.getId() == profile.getId())) {
            throw new AlreadyExists("participant in paper id=" + paper.getId());
        }
        paper.addResearcher(profile);
        profile.addPaper(paper);
        paperRepository.save(paper);
        update(profile);
        Logger.log("Add participant (" + profile.asLine() + ") to paper (" + paper.getId() + ")");
    }

    public void removeParticipant(ResearchPaper paper, ResearcherProfile profile) {
        if (paper.getResearchers().stream().noneMatch(r -> r.getId() == profile.getId())) {
            throw new DoesNotExist("participant in paper id=" + paper.getId());
        }
        paper.removeResearcher(profile);
        profile.removePaper(paper.getId());
        paperRepository.save(paper);
        update(profile);
        Logger.log("Remove participant (" + profile.asLine() + ") from paper (" + paper.getId() + ")");
    }

    public void addReference(ResearchPaper paper, ResearchPaper reference) {
        if (paper.getId() == reference.getId()) {
            throw new OperationNotAllowed("paper cannot reference itself");
        }
        if (paper.getReferences().stream().anyMatch(r -> r.getId() == reference.getId())) {
            throw new AlreadyExists("reference to paper id=" + reference.getId());
        }
        paper.addReference(reference);
        paperRepository.save(paper);
        Logger.log("Add reference (" + reference.getId() + ") to paper (" + paper.getId() + ")");
    }

    public void citePaper(ResearchPaper paper) {
        paper.setCitations(paper.getCitations() + 1);
        paperRepository.save(paper);
        Logger.log("Cite paper (" + paper.getId() + ")");
    }

    public void incrementViews(ResearchPaper paper) {
        paper.setViews(paper.getViews() + 1);
        paperRepository.save(paper);
    }

    
    
    public boolean isResearcher(User user) {
        return find(p -> p.getUser().getId() == user.getId()) != null;
    }

    public ResearcherProfile getProfile(User user) {
        ResearcherProfile match = find(p -> p.getUser().getId() == user.getId());
        if (match == null) throw new DoesNotExist("researcher profile for user id=" + user.getId());
        return match;
    }

    public List<ResearchPaper> getAllPapers() {
        return paperRepository.getAll();
    }

    public ResearchPaper getPaper(int paperId) {
        ResearchPaper paper = paperRepository.find(p -> p.getId() == paperId);
        if (paper == null) throw new DoesNotExist("paper with id=" + paperId);
        return paper;
    }

    public List<ResearchPaper> getPapersByAuthor(User user) {
        return getAllPapers().stream()
                .filter(p -> p.getResearchers().stream().anyMatch(r -> r.getUser().getId() == user.getId()))
                .toList();
    }


    @Override
    public void subscribeToEvents() {
        eventSystem.subscribe(UserCreateEvent.class, event -> {
            User createdUser = event.getUser();
            if (isResearcher(createdUser)) return;
            if (AppSettings.DEFAULT_RESEARCHER_CLASSES.contains(createdUser.getClass())) {
                createResearcherProfile(createdUser);
            }
        });

        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            User deletedUser = event.getUser();
            if (!isResearcher(deletedUser)) return;
            ResearcherProfile profile = getProfile(deletedUser);
            for (ResearchPaper paper : profile.getPapers()) {
                paper.removeResearcher(profile);
                paperRepository.save(paper);
            }
            delete(profile);
        });
    }
}
