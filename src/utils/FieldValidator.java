package utils;

import exceptions.FieldValidationError;
import exceptions.FieldValidationError.ValidationDetail;

import java.util.ArrayList;
import java.util.List;

public class FieldValidator {
    private List<ValidationDetail> errors = new ArrayList<>();

    public FieldValidator requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            errors.add(new ValidationDetail("field_required", fieldName));
        }
        return this;
    }

    public FieldValidator requirePositive(int value, String fieldName) {
        if (value <= 0) {
            errors.add(new ValidationDetail("field_positive", fieldName));
        }
        return this;
    }

    public FieldValidator requireNonNull(Object value, String fieldName) {
        if (value == null) {
            errors.add(new ValidationDetail("field_non_null", fieldName));
        }
        return this;
    }

    public FieldValidator requireInRange(double value, double min, double max, String fieldName) {
        if(Double.compare(value, min) < 0 || Double.compare(value, max) > 0){
            errors.add(new ValidationDetail("field_in_range", fieldName));
        }
        return this;
    }

    public void validate() {
        if (!errors.isEmpty()) {
            throw new FieldValidationError(errors.toArray(ValidationDetail[]::new));
        }
    }


}
