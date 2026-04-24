package exceptions;

import model.enumeration.UIMessages;

public class FieldRequiredException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldRequiredException(String fieldName) {
        super(UIMessages.ERR_FIELD_REQUIRED, fieldName);
    }
}
