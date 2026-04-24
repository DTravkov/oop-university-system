package exceptions;

import model.enumeration.UIMessages;

public class FieldOutOfRangeException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldOutOfRangeException(String fieldName) {
        super(UIMessages.ERR_FIELD_IN_RANGE, fieldName);
    }
}
