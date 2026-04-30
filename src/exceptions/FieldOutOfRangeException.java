package exceptions;

import model.enumeration.UIMessage;

public class FieldOutOfRangeException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldOutOfRangeException(String fieldName) {
        super(UIMessage.ERR_FIELD_IN_RANGE, fieldName);
    }
}
