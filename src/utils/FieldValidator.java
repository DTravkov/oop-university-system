package utils;

import exceptions.FieldValidationError;
import exceptions.FieldValidationError.ValidationDetail;
import model.enumeration.UIMessages;

import java.util.ArrayList;
import java.util.List;

public class FieldValidator {
    private List<ValidationDetail> errors = new ArrayList<>();

    public FieldValidator requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            errors.add(new ValidationDetail(UIMessages.ERR_FIELD_REQUIRED, fieldName));
        }
        return this;
    }

    public FieldValidator requirePositive(int value, String fieldName) {
        if (value <= 0) {
            errors.add(new ValidationDetail(UIMessages.ERR_FIELD_POSITIVE, fieldName));
        }
        return this;
    }

    public FieldValidator requireNonNull(Object value, String fieldName) {
        if (value == null) {
            errors.add(new ValidationDetail(UIMessages.ERR_FIELD_NON_NULL, fieldName));
        }
        return this;
    }

    public FieldValidator requireInRange(double value, double min, double max, String fieldName) {
        if(Double.compare(value, min) < 0 || Double.compare(value, max) > 0){
            errors.add(new ValidationDetail(UIMessages.ERR_FIELD_IN_RANGE, fieldName));
        }
        return this;
    }

    public void validate() {
        if (!errors.isEmpty()) {
            throw new FieldValidationError(errors.toArray(ValidationDetail[]::new));
        }
    }


}
