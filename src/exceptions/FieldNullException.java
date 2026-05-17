package exceptions;

import utils.UIText;

public class FieldNullException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldNullException(String fieldName) {
        super(UIText.ERR_FIELD_NON_NULL, fieldName);
    }
}
