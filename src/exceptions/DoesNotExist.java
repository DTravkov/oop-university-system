package exceptions;

import model.enumeration.UIMessages;

public class DoesNotExist extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public DoesNotExist(Object... args) {
        super(UIMessages.ERR_DOES_NOT_EXIST, args);
    }
}
