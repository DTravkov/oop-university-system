package exceptions;

public class AlreadyExists extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public AlreadyExists(Object... args) {
        super("already_exists", args);
    }
}
