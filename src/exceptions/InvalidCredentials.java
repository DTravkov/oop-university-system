package exceptions;

import utils.UIText;

public class InvalidCredentials extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentials() {
        super(UIText.ERR_INVALID_CREDENTIALS);
    }
}
