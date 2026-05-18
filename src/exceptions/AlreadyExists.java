package exceptions;

import utils.UIText;

public class AlreadyExists extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public AlreadyExists(String message) {
        super(message);
    }

    public AlreadyExists(UIText message) {
        super(message);
    }
}
