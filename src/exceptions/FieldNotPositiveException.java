package exceptions;

import model.enumeration.UIMessages;

public class FieldNotPositiveException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldNotPositiveException(String fieldName) {
        super(UIMessages.ERR_FIELD_POSITIVE, fieldName);
    }
}
