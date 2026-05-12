package model.domain;

import settings.AppSettings;


public class DeletedResearcherProfile extends ResearcherProfile {

    private static final long serialVersionUID = 1L;

    public DeletedResearcherProfile() {
        super(AppSettings.DELETED_USER);
        this.id = AppSettings.DELETED_RESEARCHER_PROFILE_ID;
    }

    @Override
    public String asLine() {
        return "ID: " + id + " | Deleted Researcher Profile";
    }

    @Override
    public String asTable() {
        return "\nDeleted Researcher Profile\n";
    }
}
