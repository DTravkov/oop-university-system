package exceptions;

public class OperationNotAllowed extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OperationNotAllowed(Object... args) {
        super("operation_not_allowed", args);
    }
}
