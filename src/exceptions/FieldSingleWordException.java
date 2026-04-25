package exceptions;

import model.enumeration.UIMessages;

public class FieldSingleWordException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldSingleWordException(String fieldName) {
        super(UIMessages.ERR_FIELD_SINGLE_WORD, fieldName);
    }
}
