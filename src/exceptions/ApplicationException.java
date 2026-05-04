package exceptions;

import model.enumeration.UIMessage;
import utils.Translator;

public abstract class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final UIMessage message;
    private final Object[] args;


    protected ApplicationException(UIMessage message, Object... args) {
        this.message = message;
        this.args = (args != null) ? args : new Object[0];
            
    }

    @Override
    public String getMessage() {
        return Translator.translate(message, args);
    }
}
