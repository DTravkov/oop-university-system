package exceptions;

import utils.UIText;

public class FieldSingleWordException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FieldSingleWordException(String fieldName) {
        super(UIText.ERR_FIELD_SINGLE_WORD, fieldName);
    }
}
