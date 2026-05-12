package model.domain;

import java.util.Date;

import settings.AppSettings;


public class DeletedStudent extends Student {

    private static final long serialVersionUID = 1L;

    public DeletedStudent() {
        super("DELETED_STUDENT", "Deleted", "Deleted", "Student", new Date(0));
        setBanned(true);
        this.id = AppSettings.DELETED_STUDENT_ID;
    }

    @Override
    public String asLine() {
        return super.asLine() + " | Deleted Student";
    }

    @Override
    public String asTable() {
        return super.asTable() + "Deleted Student\n";
    }
}
