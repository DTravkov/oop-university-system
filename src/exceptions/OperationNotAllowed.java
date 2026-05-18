package exceptions;

import utils.UIText;

public class OperationNotAllowed extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OperationNotAllowed(String message) {
        super(message);
    }

    public OperationNotAllowed(UIText message) {
        super(message);
    }
}
