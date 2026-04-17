package exceptions;

import services.LanguageService;

public abstract class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String messageKey;
    private final Object[] args;

    protected ApplicationException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args != null ? args.clone() : new Object[0];
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args.clone();
    }

    @Override
    public String getMessage() {
        return LanguageService.translate(messageKey, args);
    }
}
