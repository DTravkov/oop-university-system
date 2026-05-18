package exceptions;

import utils.UIText;

public abstract class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final UIText messageKey;
    private final String plainMessage;
    private final Object[] args;

    protected ApplicationException(String message) {
        this.plainMessage = message;
        this.messageKey = null;
        this.args = new Object[0];
    }

    protected ApplicationException(UIText message, Object... args) {
        this.plainMessage = null;
        this.messageKey = message;
        this.args = (args != null) ? args : new Object[0];
    }

    @Override
    public String getMessage() {
        if (plainMessage != null) {
            return plainMessage;
        }
        return messageKey.localized(args);
    }
}
