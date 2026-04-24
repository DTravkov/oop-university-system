package exceptions;

import model.enumeration.UIMessages;

public class FieldNullException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldNullException(String fieldName) {
        super(UIMessages.ERR_FIELD_NON_NULL, fieldName);
    }
}
