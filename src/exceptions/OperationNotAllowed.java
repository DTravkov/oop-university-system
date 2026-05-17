package exceptions;

import utils.UIText;

public class OperationNotAllowed extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OperationNotAllowed(Object... args) {
        super(UIText.ERR_OPERATION_NOT_ALLOWED, args);
    }
}
