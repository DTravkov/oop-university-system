package exceptions;

import utils.UIText;

public class AlreadyExists extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public AlreadyExists(Object... args) {
        super(UIText.ERR_ALREADY_EXISTS, args);
    }
}
