package exceptions;

import model.enumeration.UIMessages;

public class ImmutableFieldChanged extends ApplicationException {

    public ImmutableFieldChanged() {
        super(UIMessages.ERR_IMMUTABLE_ID);
    }
}
