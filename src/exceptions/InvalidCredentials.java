package exceptions;

import model.enumeration.UIMessages;

public class InvalidCredentials extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentials() {
        super(UIMessages.ERR_INVALID_CREDENTIALS);
    }

    public InvalidCredentials(Object... args) {
        super(UIMessages.ERR_INVALID_CREDENTIALS, args);
    }
}
