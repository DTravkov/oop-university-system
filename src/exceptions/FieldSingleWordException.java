package exceptions;

import model.enumeration.UIMessage;

public class FieldSingleWordException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldSingleWordException(String fieldName) {
        super(UIMessage.ERR_FIELD_SINGLE_WORD, fieldName);
    }
}
