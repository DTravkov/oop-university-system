package exceptions;

import java.util.Arrays;

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

        return Arrays.stream(details)
            .map(detail -> LanguageService.translate(detail.getMessageKey(), detail.getArgs()))
            .reduce((left, right) -> left + "; " + right)
            .orElse("");
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
