package exceptions;

import utils.UIText;

public class ImmutableFieldChanged extends ApplicationException {

    public ImmutableFieldChanged() {
        super(UIText.ERR_IMMUTABLE_ID);
    }
}
