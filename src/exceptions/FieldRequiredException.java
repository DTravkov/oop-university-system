package exceptions;

import model.enumeration.UIMessage;

public class FieldRequiredException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldRequiredException(String fieldName) {
        super(UIMessage.ERR_FIELD_REQUIRED, fieldName);
    }
}
