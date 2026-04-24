package exceptions;

import model.enumeration.UIMessages;
import services.LanguageService;

public class FieldValidationError extends ApplicationException {

    private static final long serialVersionUID = 1L;

    private final ValidationDetail[] details;

    public FieldValidationError(ValidationDetail... details) {
        super(UIMessages.ERR_FIELD_VALIDATION, formatDetails(details));
        this.details = details != null ? details.clone() : new ValidationDetail[0];
    }

    public ValidationDetail[] getDetails() {
        return details.clone();
    }

    private static String formatDetails(ValidationDetail[] details) {
        if (details == null || details.length == 0) {
            return "";
        }

        String detailString  = "";
        for(ValidationDetail detail : details){
            detailString += LanguageService.translate(detail.getMessageKey(), detail.getArgs());
        }
        return detailString;
    }

    public static class ValidationDetail {
        private final UIMessages messageKey;
        private final Object[] args;

        public ValidationDetail(UIMessages messageKey, Object... args) {
            this.messageKey = messageKey;
            this.args = args != null ? args.clone() : new Object[0];
        }

        public UIMessages getMessageKey() {
            return messageKey;
        }

        public Object[] getArgs() {
            return args.clone();
        }
    }

}
