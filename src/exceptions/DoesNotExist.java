package exceptions;

import utils.UIText;

public class DoesNotExist extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public DoesNotExist(String message) {
        super(message);
    }

    public DoesNotExist(UIText message) {
        super(message);
    }
}
