package exceptions;

import model.enumeration.UIMessage;

public class ImmutableFieldChanged extends ApplicationException {

    public ImmutableFieldChanged() {
        super(UIMessage.ERR_IMMUTABLE_ID);
    }
}
