package exceptions;

import model.enumeration.UIMessage;

public class FieldNullException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldNullException(String fieldName) {
        super(UIMessage.ERR_FIELD_NON_NULL, fieldName);
    }
}
