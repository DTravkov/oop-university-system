package application;

import exceptions.ApplicationException;
import model.domain.GraduateStudent;
import model.domain.User;
import model.domain.ResearchPaper;
import model.domain.ResearchProject;
import model.domain.ResearcherProfile;
import model.enumeration.UIMessage;
import services.ResearchService;
import services.UserService;
import utils.Translator;
import utils.UIForms;

public final class ResearchApp extends BaseApp {

    private static final ResearchService researchService = services.researchService;
    private static final UserService userService = services.userService;

    private ResearchApp() {
    }

    public static void startApp() {
        while (true) {
            printMainMenu();
            String choice = readChoice(UIMessage.MENU_CHOOSE, 1, 4);
            switch (choice) {
                case "1":
                    startResearcherMenu();
                    break;
                case "2":
                    startProjectMenu();
                    break;
                case "3":
                    startPaperMenu();
                    break;
                case "4":
                    return;
                default:
                    printInvalidChoice();
            }
        }
    }

    private static void printMainMenu() {
        println("\n|||  Research App |||");
        println("1. Researcher Profile menu");
        println("2. Project menu");
        println("3. Paper menu");
        println("4. " + Translator.translate(UIMessage.MENU_EXIT));
    }

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
        printAllResearchers();
        int projectId = UIForms.readInt(scanner, UIMessage.INPUT_PROJECT_ID);
        int researcherUserId = UIForms.readInt(scanner, UIMessage.INPUT_RESEARCHER_USER_ID);
        researchService.removeParticipantFromProject(projectId, researcherUserId);
        println(Translator.translate(UIMessage.MSG_DELETED));
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
