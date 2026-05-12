package model.domain;

import settings.AppSettings;


public class DeletedTechSupportSpecialist extends TechSupportSpecialist {

    private static final long serialVersionUID = 1L;

    public DeletedTechSupportSpecialist() {
        super("DELETED_SPECIALIST", "Deleted", "Deleted", "Specialist");
        setBanned(true);
        this.id = AppSettings.DELETED_TECH_SUPPORT_SPECIALIST_ID;
    }

    @Override
    public String asLine() {
        return super.asLine() + " | Deleted Tech Support Specialist";
    }

    @Override
    public String asTable() {
        return super.asTable() + "Deleted Tech Support Specialist\n";
    }
}
