package utils;

import exceptions.FieldNotPositiveException;
import exceptions.FieldNullException;
import exceptions.FieldOutOfRangeException;
import exceptions.FieldRequiredException;
import exceptions.FieldSingleWordException;

public final class FieldValidator {

    private FieldValidator() {
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new FieldRequiredException(fieldName);
        }
    }

    public static void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new FieldNotPositiveException(fieldName);
        }
    }

    public static void requirePositive(double value, String fieldName) {
        if (Double.compare(value, 0) <= 0) {
            throw new FieldNotPositiveException(fieldName);
        }
    }

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new FieldNullException(fieldName);
        }
    }

    public static void requireInRange(double value, double min, double max, String fieldName) {
        if (Double.compare(value, min) < 0 || Double.compare(value, max) > 0) {
            throw new FieldOutOfRangeException(fieldName);
        }
    }

    public static void requireSingleWord(String value, String fieldName) {
        requireNonBlank(value, fieldName);
        String trimmed = value.trim();
        if (trimmed.contains(" ") || trimmed.contains("\t")) {
            throw new FieldSingleWordException(fieldName);
        }
    }
}
