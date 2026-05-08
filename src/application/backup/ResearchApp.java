package application;

import java.util.List;

import exceptions.ApplicationException;
import exceptions.DoesNotExist;
import model.domain.GraduateStudent;
import model.domain.User;
import model.domain.ResearchPaper;
import model.domain.ResearchProject;
import model.domain.ResearcherProfile;
import model.enumeration.UIMessage;
import services.ResearchService;
import services.UserService;
import settings.AppSettings;
import utils.Translator;
import utils.UIForms;

public final class ResearchApp extends BaseApp {

    private static final ResearchService researchService = services.researchService;
    private static final UserService userService = services.userService;

    private ResearchApp() {
    }

    public static void startApp() {
        User activeUser = AppSettings.getActiveUser();
        while (true) {
            if (researchService.isResearcher(activeUser.getId())) {
                printMainMenu();
                String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 5);
                try {
                    switch (choice) {
                        case "1":
                            viewMyResearcherProfile(activeUser.getId());
                            break;
                        case "2":
                            startMyResearcherProjectsMenu();
                            break;
                        case "3":
                            startMyResearcherPapersMenu();
                            break;
                        case "4":
                            viewAllProjectsMenu();
                            break;
                        case "5":
                            return;
                        default:
                            printInvalidChoice();
                    }
                } catch (ApplicationException e) {
                    printExceptionDetails(e);
                }
            } else {
                printBecomeResearcherMenu();
                String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 2);
                try {
                    switch (choice) {
                        case "1":
                            researchService.createResearcher(activeUser.getId());
                            println(Translator.translate(UIMessage.MSG_CREATED));
                            println(researchService.getResearcherDTO(activeUser.getId()));
                            break;
                        case "2":
                            return;
                        default:
                            printInvalidChoice();
                    }
                } catch (ApplicationException e) {
                    printExceptionDetails(e);
                }
            }
        }
    }

    private static void printBecomeResearcherMenu() {
        println("\n|||  Research App |||");
        println("1. Become a researcher");
        println("2. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void printMainMenu() {
        println("\n|||  Research App |||");
        println("1. View my Researcher profile");
        println("2. Manage my researcher projects");
        println("3. Manage my researcher papers");
        println("4. View all projects");
        println("5. " + Translator.translate(UIMessage.MENU_EXIT));
    }

    private static void viewMyResearcherProfile(int userId) {
        println(researchService.getResearcherDTO(userId));
    }

    private static void viewAllProjectsMenu() {
        while (true) {
            try {
                printAllProjects();
                int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
                openProjectForReading(projectId);
                println("1. Cite a paper from this project");
                println("2. Back");
                String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 2);
                switch (choice) {
                    case "1":
                        citePaperFromProject(projectId);
                        break;
                    case "2":
                        return;
                    default:
                        printInvalidChoice();
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    private static void openProjectForReading(int projectId) {
        ResearchProject project = researchService.getProject(projectId);
        for (Integer paperId : project.getPapers()) {
            ResearchPaper paper = researchService.getPaper(paperId);
            paper.addView();
            researchService.update(paper);
        }
        println(researchService.getProjectDTO(projectId).toString());
    }

    private static void citePaperFromProject(int projectId) {
        ResearchProject project = researchService.getProject(projectId);
        List<Integer> paperIds = project.getPapers();
        if (paperIds.isEmpty()) {
            throw new DoesNotExist("papers for project with id=" + projectId);
        }
        println("|||  Papers in project |||");
        paperIds.forEach(paperId -> println(researchService.getPaperDTO(paperId).toShortString()));
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        if (!paperIds.contains(paperId)) {
            throw new DoesNotExist("paper with id=" + paperId + " in project with id=" + projectId);
        }
        ResearchPaper paper = researchService.getPaper(paperId);
        paper.addCitation();
        researchService.update(paper);
        println(Translator.translate(UIMessage.SUCCESS));
    }

    private static void startMyResearcherProjectsMenu() {
        int activeUserId = AppSettings.getActiveUser().getId();
        while (true) {
            printMyResearcherProjectsMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 7);
            try {
                switch (choice) {
                    case "1":
                        viewAllMyProjects(activeUserId);
                        break;
                    case "2":
                        viewAllPapersFromMyProject(activeUserId);
                        break;
                    case "3":
                        addNewProject(activeUserId);
                        break;
                    case "4":
                        deleteMyProject(activeUserId);
                        break;
                    case "5":
                        addParticipantToMyProject(activeUserId);
                        break;
                    case "6":
                        removeParticipantFromMyProject(activeUserId);
                        break;
                    case "7":
                        return;
                    default:
                        printInvalidChoice();
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    private static void startMyResearcherPapersMenu() {
        int activeUserId = AppSettings.getActiveUser().getId();
        while (true) {
            printMyResearcherPapersMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 5);
            try {
                switch (choice) {
                    case "1":
                        createNewMyPaper(activeUserId);
                        break;
                    case "2":
                        deleteMyPaper(activeUserId);
                        break;
                    case "3":
                        assignMyPaperToMyProject(activeUserId);
                        break;
                    case "4":
                        deletePaperFromMyProject(activeUserId);
                        break;
                    case "5":
                        return;
                    default:
                        printInvalidChoice();
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    private static void printMyResearcherProjectsMenu() {
        println("\n|||  Projects |||");
        println("1. View all my projects");
        println("2. View all papers from a project");
        println("3. Add new project");
        println("4. Delete my project");
        println("5. Add participant to project");
        println("6. Delete participant from project");
        println("7. Back");
    }

    private static void printMyResearcherPapersMenu() {
        println("\n|||  Papers |||");
        println("1. Create new paper");
        println("2. Delete my paper");
        println("3. Assign my paper to my project");
        println("4. Delete paper from my project");
        println("5. Back");
    }

    private static void addNewProject(int activeUserId) {
        String topic = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PROJECT_TOPIC);
        ResearchProject project = researchService.createProject(new ResearchProject(topic));
        researchService.addParticipantToProject(project.getId(), activeUserId);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(researchService.getProjectDTO(project.getId()));
    }

    private static void viewAllMyProjects(int activeUserId) {
        List<ResearchProject> myProjects = getMyResearcherProjects(activeUserId);
        printProjects(myProjects);
    }

    private static void viewAllPapersFromMyProject(int activeUserId) {
        List<ResearchProject> myProjects = getMyResearcherProjects(activeUserId);
        printProjects(myProjects);
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        ensureMyProject(myProjects, projectId);

        ResearchProject project = researchService.getProject(projectId);
        List<Integer> paperIds = project.getPapers();
        if (paperIds.isEmpty()) {
            throw new DoesNotExist("papers for project with id=" + projectId);
        }
        println("|||  Papers From Project with ID: " + projectId + " |||");
        paperIds.forEach(paperId -> println(researchService.getPaperDTO(paperId)));
    }

    private static void deleteMyProject(int activeUserId) {
        List<ResearchProject> myProjects = getMyResearcherProjects(activeUserId);
        printProjects(myProjects);
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        ensureMyProject(myProjects, projectId);
        researchService.deleteProject(projectId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void addParticipantToMyProject(int activeUserId) {
        List<ResearchProject> myProjects = getMyResearcherProjects(activeUserId);
        printProjects(myProjects);
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        printAllResearchers();
        ensureMyProject(myProjects, projectId);
        int researcherUserId = UIForms.readInt(scanner, UIMessage.INPUT_RESEARCHER_USER_ID);
        researchService.addParticipantToProject(projectId, researcherUserId);
        println(Translator.translate(UIMessage.MSG_CREATED));
    }

    private static void removeParticipantFromMyProject(int activeUserId) {
        List<ResearchProject> myProjects = getMyResearcherProjects(activeUserId);
        printProjects(myProjects);
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        ensureMyProject(myProjects, projectId);
        printProjectParticipants(projectId);
        int researcherUserId = UIForms.readInt(scanner, UIMessage.INPUT_RESEARCHER_USER_ID);
        researchService.removeParticipantFromProject(projectId, researcherUserId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static List<ResearchProject> getMyResearcherProjects(int activeUserId) {
        List<ResearchProject> myProjects = researchService.getResearcherProjects(activeUserId);
        if (myProjects.isEmpty()) {
            throw new DoesNotExist("projects for researcher with id=" + activeUserId);
        }
        return myProjects;
    }

    private static void printProjects(List<ResearchProject> projects) {
        println("|||  My projects |||");
        projects.forEach(project -> println(researchService.getProjectDTO(project.getId()).toShortString()));
    }

    private static void ensureMyProject(List<ResearchProject> myProjects, int projectId) {
        boolean projectInList = myProjects.stream().anyMatch(project -> project.getId() == projectId);
        if (!projectInList) {
            throw new DoesNotExist("my project with id=" + projectId);
        }
    }

    private static void createNewMyPaper(int activeUserId) {
        ResearchPaper paper = researchService.createPaper(new ResearchPaper());
        researchService.addParticipantToPaper(paper.getId(), activeUserId);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(researchService.getPaperDTO(paper.getId()));
    }

    private static void deleteMyPaper(int activeUserId) {
        List<ResearchPaper> myPapers = getMyResearcherPapers(activeUserId);
        printPapers(myPapers);
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        ensureMyPaper(myPapers, paperId);
        researchService.deletePaper(paperId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void assignMyPaperToMyProject(int activeUserId) {
        List<ResearchPaper> myPapers = getMyResearcherPapers(activeUserId);
        List<ResearchProject> myProjects = getMyResearcherProjects(activeUserId);
        printPapers(myPapers);
        printProjects(myProjects);
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        ensureMyPaper(myPapers, paperId);
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        ensureMyProject(myProjects, projectId);
        researchService.addPaperToProject(projectId, paperId);
        println(Translator.translate(UIMessage.MSG_CREATED));
    }

    private static void deletePaperFromMyProject(int activeUserId) {
        List<ResearchPaper> myPapers = getMyResearcherPapers(activeUserId);
        List<ResearchProject> myProjects = getMyResearcherProjects(activeUserId);
        printPapers(myPapers);
        printProjects(myProjects);
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        ensureMyPaper(myPapers, paperId);
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        ensureMyProject(myProjects, projectId);
        researchService.removePaperFromProject(projectId, paperId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static List<ResearchPaper> getMyResearcherPapers(int activeUserId) {
        List<ResearchPaper> myPapers = researchService.getResearcherPapers(activeUserId);
        if (myPapers.isEmpty()) {
            throw new DoesNotExist("papers for researcher with id=" + activeUserId);
        }
        return myPapers;
    }

    private static void printPapers(List<ResearchPaper> papers) {
        println("|||  My papers |||");
        papers.forEach(paper -> println(researchService.getPaperDTO(paper.getId()).toShortString()));
    }

    private static void ensureMyPaper(List<ResearchPaper> myPapers, int paperId) {
        boolean paperInList = myPapers.stream().anyMatch(paper -> paper.getId() == paperId);
        if (!paperInList) {
            throw new DoesNotExist("my paper with id=" + paperId);
        }
    }

    @SuppressWarnings("unused")
    private static void startResearcherMenu() {
        while (true) {
            printResearcherMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 5);
            try {
                switch (choice) {
                    case "1":
                        return;
                    case "2":
                        printAllResearchers();
                        break;
                    case "3":
                        addResearcherProfile();
                        break;
                    case "4":
                        assignSupervisorToGraduateStudent();
                        break;
                    case "5":
                        printGraduateStudentSupervisor();
                        break;
                    default:
                        printInvalidChoice();
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    @SuppressWarnings("unused")
    private static void startProjectMenu() {
        while (true) {
            printProjectMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 9);
            try {
                switch (choice) {
                    case "1":
                        return;
                    case "2":
                        printAllProjects();
                        break;
                    case "3":
                        printProjectDetails();
                        break;
                    case "4":
                        createProject();
                        break;
                    case "5":
                        deleteProject();
                        break;
                    case "6":
                        addProjectParticipant();
                        break;
                    case "7":
                        removeProjectParticipant();
                        break;
                    case "8":
                        addPaperToProject();
                        break;
                    case "9":
                        removePaperFromProject();
                        break;
                    default:
                        printInvalidChoice();
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    @SuppressWarnings("unused")
    private static void startPaperMenu() {
        while (true) {
            printPaperMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 9);
            try {
                switch (choice) {
                    case "1":
                        return;
                    case "2":
                        printAllPapers();
                        break;
                    case "3":
                        printPaperDetails();
                        break;
                    case "4":
                        createPaper();
                        break;
                    case "5":
                        deletePaper();
                        break;
                    case "6":
                        incrementPaperViews();
                        break;
                    case "7":
                        incrementPaperCitations();
                        break;
                    case "8":
                        addPaperParticipant();
                        break;
                    case "9":
                        removePaperParticipant();
                        break;
                    default:
                        printInvalidChoice();
                }
            } catch (ApplicationException e) {
                printExceptionDetails(e);
            }
        }
    }

    private static void printResearcherMenu() {
        println("\n|||  Researcher Profile Menu |||");
        println("1. Back");
        println("2. Get all researchers");
        println("3. Add researcher profile");
        println("4. Assign supervisor to graduate student");
        println("5. Get graduate student supervisor");
    }


    private static void printAllGraduateStudents() {
        println("|||  Graduate students |||");
        for (var user : userService.getAllByClassOrSubclass(GraduateStudent.class)) {
            println(userService.getDTO(user).toShortString());
        }
    }

    private static void assignSupervisorToGraduateStudent() {
        printAllGraduateStudents();
        int gradStudentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        printAllResearchers();
        int supervisorId = UIForms.readInt(scanner, UIMessage.INPUT_SUPERVISOR_ID);
        researchService.assignSupervisor(gradStudentId, supervisorId);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(userService.getDTO(gradStudentId));
        println(userService.getDTO(supervisorId));
    }

    private static void printGraduateStudentSupervisor() {
        printAllGraduateStudents();
        int gradStudentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        User supervisor = researchService.getSupervisorByStudentId(gradStudentId);
        println(userService.getDTO(supervisor).toString());
    }

    private static void addResearcherProfile() {
        printAllUsers();
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_RESEARCH_PROFILE_USER_ID);
        ResearcherProfile profile = researchService.createResearcher(userId);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(researchService.getResearcherDTO(profile.getUserId()));
    }

    private static void printProjectMenu() {
        println("\n|||  Project Menu |||");
        println("1. Back");
        println("2. Get all projects");
        println("3. Get project details");
        println("4. Create project");
        println("5. Delete project");
        println("6. Add participant");
        println("7. Remove participant");
        println("8. Add paper");
        println("9. Remove paper");
    }

    private static void printProjectDetails() {
        printAllProjects();
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        println(researchService.getProjectDTO(projectId).toString());
    }

    private static void createProject() {
        String topic = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PROJECT_TOPIC);
        ResearchProject project = researchService.createProject(new ResearchProject(topic));
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(researchService.getProjectDTO(project.getId()));
    }

    private static void deleteProject() {
        printAllProjects();
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        researchService.deleteProject(projectId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void addProjectParticipant() {
        printAllProjects();
        printAllResearchers();
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        int researcherUserId = UIForms.readInt(scanner, UIMessage.INPUT_RESEARCHER_USER_ID);
        researchService.addParticipantToProject(projectId, researcherUserId);
        println(Translator.translate(UIMessage.MSG_CREATED));
    }

    private static void removeProjectParticipant() {
        printAllProjects();
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        printProjectParticipants(projectId);
        int researcherUserId = UIForms.readInt(scanner, UIMessage.INPUT_RESEARCHER_USER_ID);
        researchService.removeParticipantFromProject(projectId, researcherUserId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void printProjectParticipants(int projectId) {
        ResearchProject project = researchService.getProject(projectId);
        List<Integer> participantIds = project.getParticipants();
        if (participantIds.isEmpty()) {
            throw new DoesNotExist("participants for project with id=" + projectId);
        }
        println("|||  Project participants |||");
        participantIds.forEach(userId -> println(userService.getDTO(userId).toShortString()));
    }

    private static void addPaperToProject() {
        printAllProjects();
        printAllPapers();
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        researchService.addPaperToProject(projectId, paperId);
        println(Translator.translate(UIMessage.MSG_CREATED));
    }

    private static void removePaperFromProject() {
        printAllProjects();
        printAllPapers();
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        researchService.removePaperFromProject(projectId, paperId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void printPaperMenu() {
        println("\n|||  Paper Menu |||");
        println("1. Back");
        println("2. List all papers");
        println("3. Get paper details");
        println("4. Create paper");
        println("5. Delete paper");
        println("6. Increment views");
        println("7. Increment citations");
        println("8. Add participant");
        println("9. Remove participant");
    }

    private static void printPaperDetails() {
        printAllPapers();
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        println(researchService.getPaperDTO(paperId).toString());
    }

    private static void createPaper() {
        ResearchPaper paper = researchService.createPaper(new ResearchPaper());
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(researchService.getPaperDTO(paper.getId()));
    }

    private static void deletePaper() {
        printAllPapers();
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        researchService.deletePaper(paperId);
        println(Translator.translate(UIMessage.MSG_DELETED));
    }

    private static void incrementPaperViews() {
        printAllPapers();
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        ResearchPaper paper = researchService.getPaper(paperId);
        paper.addView();
        researchService.update(paper);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(researchService.getPaperDTO(paperId));
    }

    private static void incrementPaperCitations() {
        printAllPapers();
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        ResearchPaper paper = researchService.getPaper(paperId);
        paper.addCitation();
        researchService.update(paper);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(researchService.getPaperDTO(paperId));
    }

    private static void addPaperParticipant() {
        printAllPapers();
        printAllResearchers();
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        int researcherUserId = UIForms.readInt(scanner, UIMessage.INPUT_RESEARCHER_USER_ID);
        researchService.addParticipantToPaper(paperId, researcherUserId);
        println(Translator.translate(UIMessage.MSG_CREATED));
        println(researchService.getPaperDTO(paperId));
    }

    private static void removePaperParticipant() {
        printAllPapers();
        printAllResearchers();
        int paperId = UIForms.readInt(scanner, UIMessage.INPUT_PAPER_ID);
        int researcherUserId = UIForms.readInt(scanner, UIMessage.INPUT_RESEARCHER_USER_ID);
        researchService.removeParticipantFromPaper(paperId, researcherUserId);
        println(Translator.translate(UIMessage.MSG_DELETED));
        println(researchService.getPaperDTO(paperId));
    }

    private static void printAllUsers(){
        println("|||  Users |||");
        for(var user : userService.getAll()){
            println(userService.getDTO(user).toShortString());
        }
        
    }

    private static void printAllResearchers() {
        println("|||  Researchers |||");
        for (var profile : researchService.getAllResearchers()) {
            println(userService.getDTO(profile.getUserId()).toShortString() + " | h-index: " + researchService.calculateHIndex(profile.getUserId()));
        }
    }

    private static void printAllProjects() {
        println("|||  Projects |||");
        for (ResearchProject project : researchService.getAllProjects()) {
            println(researchService.getProjectDTO(project.getId()).toShortString());
        }
    }

    private static void printAllPapers() {
        println("|||  Papers |||");
        for (ResearchPaper paper : researchService.getAllPapers()) {
            println(researchService.getPaperDTO(paper.getId()).toShortString());
        }
    }
}
