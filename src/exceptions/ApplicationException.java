package exceptions;

import utils.UIText;

public abstract class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final UIText message;
    private final Object[] args;


    protected ApplicationException(UIText message, Object... args) {
        this.message = message;
        this.args = (args != null) ? args : new Object[0];
            
    }

    @Override
    public String getMessage() {
        return message.localized(args);
    }
}
