package exceptions;

import utils.UIText;

public class UserBannedOrDeleted extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public UserBannedOrDeleted() {
        super(UIText.ERR_USER_BANNED_OR_DELETED);
    }

    public UserBannedOrDeleted(Object... args) {
        super(UIText.ERR_USER_BANNED_OR_DELETED, args);
    }
}
