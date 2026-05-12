package model.domain;

import model.enumeration.TeacherType;
import settings.AppSettings;


public class DeletedTeacher extends Teacher {

    private static final long serialVersionUID = 1L;

    public DeletedTeacher() {
        super("DELETED_TEACHER", "Deleted", "Deleted", "Teacher", TeacherType.BOTH);
        setBanned(true);
        this.id = AppSettings.DELETED_TEACHER_ID;
    }

    @Override
    public String asLine() {
        return super.asLine() + " | Deleted Teacher";
    }

    @Override
    public String asTable() {
        return super.asTable() + "Deleted Teacher\n";
    }
}
