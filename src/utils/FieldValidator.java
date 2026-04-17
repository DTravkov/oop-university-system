package utils;

import exceptions.FieldValidationError;
import java.util.ArrayList;
import java.util.List;

public class FieldValidator {
    private List<String> errors = new ArrayList<>();

    public FieldValidator requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            errors.add(fieldName + " is required");
        }
        return this;
    }

    public FieldValidator requirePositive(int value, String fieldName) {
        if (value <= 0) {
            errors.add(fieldName + " must be positive");
        }
        return this;
    }

    public FieldValidator requireNonNull(Object value, String fieldName) {
        if (value == null) {
            errors.add(fieldName + " cannot be null");
        }
        return this;
    }

    public void validate() {
        if (!errors.isEmpty()) {
            throw new FieldValidationError(String.join(", ", errors));
        }
    }
}