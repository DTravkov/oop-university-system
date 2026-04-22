package exceptions;

import services.LanguageService;

public class FieldValidationError extends ApplicationException {

    private static final long serialVersionUID = 1L;

    private final ValidationDetail[] details;

    public FieldValidationError(ValidationDetail... details) {
        super("field_validation", formatDetails(details));
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
        private final String messageKey;
        private final Object[] args;

        public ValidationDetail(String messageKey, Object... args) {
            this.messageKey = messageKey;
            this.args = args != null ? args.clone() : new Object[0];
        }

        public String getMessageKey() {
            return messageKey;
        }

        public Object[] getArgs() {
            return args.clone();
        }
    }

}
