package utils;

import exceptions.FieldValidationError;

/**
 * FieldValidator has many helpers for validation data and throwing exceptions if data is invalid.
 * Was created for domain methods, to quickly check some restrictions.
 */
public final class FieldValidator {

    private FieldValidator() {
    }

    public static void requirePasswordValidation(String value) {
        if (value == null || value.isBlank() || value.length() < 8 || value.toLowerCase().equals(value)) {
            throw new FieldValidationError(UIText.ERR_FIELD_PASSWORD_WEAK);
        }
    }

    public static void requireNonBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new FieldValidationError(UIText.ERR_FIELD_EMPTY);
        }
    }

    public static void requireSingleWord(String value) {
        requireNonBlank(value);
        String trimmed = value.trim();
        if (trimmed.contains(" ") || trimmed.contains("\t")) {
            throw new FieldValidationError(UIText.ERR_FIELD_SINGLE_WORD);
        }
    }

    public static void requirePositive(int value) {
        if (value <= 0) {
            throw new FieldValidationError(UIText.ERR_FIELD_POSITIVE);
        }
    }

    public static void requirePositive(double value) {
        if (Double.compare(value, 0) <= 0) {
            throw new FieldValidationError(UIText.ERR_FIELD_POSITIVE);
        }
    }

    public static void requireInRange(double value, double min, double max) {
        if (Double.compare(value, min) < 0 || Double.compare(value, max) > 0) {
            throw new FieldValidationError(UIText.ERR_FIELD_OUT_OF_RANGE);
        }
    }

    public static void requireNonNull(Object value) {
        if (value == null) {
            throw new FieldValidationError(UIText.ERR_FIELD_NULL);
        }
    }
}
