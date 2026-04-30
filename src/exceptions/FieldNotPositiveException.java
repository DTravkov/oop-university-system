package exceptions;

import model.enumeration.UIMessage;

public class FieldNotPositiveException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldNotPositiveException(String fieldName) {
        super(UIMessage.ERR_FIELD_POSITIVE, fieldName);
    }
}
