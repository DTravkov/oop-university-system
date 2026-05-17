package exceptions;

import utils.UIText;

public class FieldRequiredException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldRequiredException(String fieldName) {
        super(UIText.ERR_FIELD_REQUIRED, fieldName);
    }
}
