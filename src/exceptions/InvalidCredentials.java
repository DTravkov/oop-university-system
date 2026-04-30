package exceptions;

import model.enumeration.UIMessage;

public class InvalidCredentials extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentials() {
        super(UIMessage.ERR_INVALID_CREDENTIALS);
    }

    public InvalidCredentials(Object... args) {
        super(UIMessage.ERR_INVALID_CREDENTIALS, args);
    }
}
