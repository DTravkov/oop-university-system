package exceptions;

import utils.UIText;

public class FieldValidationError extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldValidationError(UIText message) {
        super(message);
    }
}
