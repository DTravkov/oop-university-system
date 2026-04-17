package exceptions;

public class DoesNotExist extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public DoesNotExist(Object... args) {
        super("does_not_exist", args);
    }
}
