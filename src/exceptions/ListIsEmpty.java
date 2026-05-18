package exceptions;

import utils.UIText;

public class ListIsEmpty extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public ListIsEmpty() {
        super(UIText.ERR_LIST_IS_EMPTY);
    }
}
