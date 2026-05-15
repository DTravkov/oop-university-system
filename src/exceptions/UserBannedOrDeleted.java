package exceptions;

import model.enumeration.UIMessage;

public class UserBannedOrDeleted extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public UserBannedOrDeleted() {
        super(UIMessage.ERR_USER_BANNED_OR_DELETED);
    }

    public UserBannedOrDeleted(Object... args) {
        super(UIMessage.ERR_USER_BANNED_OR_DELETED, args);
    }
}
