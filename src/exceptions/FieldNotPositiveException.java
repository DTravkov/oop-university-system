package exceptions;

import utils.UIText;

public class FieldNotPositiveException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldNotPositiveException(String fieldName) {
        super(UIText.ERR_FIELD_POSITIVE, fieldName);
    }
}
