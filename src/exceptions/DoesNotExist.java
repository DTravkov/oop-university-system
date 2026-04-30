package exceptions;

import model.enumeration.UIMessage;

public class DoesNotExist extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public DoesNotExist(Object... args) {
        super(UIMessage.ERR_DOES_NOT_EXIST, args);
    }
}
