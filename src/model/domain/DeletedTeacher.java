package model.domain;

import model.enumeration.TeacherType;
import settings.AppSettings;

/**
 * System placeholder {@link Teacher} for enrollments when the real teacher account was removed.
 * {@link DeletedUser} cannot be used here because enrollment requires a {@link Teacher} reference.
 */
public class DeletedTeacher extends Teacher {

    private static final long serialVersionUID = 1L;

    public DeletedTeacher() {
        super("DELETED_TEACHER", "DELETED", "DELETED", "USER", TeacherType.BOTH);
        setBanned(true);
        this.id = AppSettings.DELETED_TEACHER_ID;
    }

    @Override
    public String asLine() {
        return super.asLine() + " | [Deleted teacher placeholder]";
    }

    @Override
    public String asTable() {
        return super.asTable() + "Note: DeletedTeacher (system placeholder)\n";
    }
}
