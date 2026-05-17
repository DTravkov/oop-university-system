package exceptions;

import utils.UIText;

public class DoesNotExist extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public DoesNotExist(Object... args) {
        super(UIText.ERR_DOES_NOT_EXIST, args);
    }
}
