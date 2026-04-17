package exceptions;

public class InvalidCredentials extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentials(Object... args) {
        super("invalid_credentials", args);
    }
}
