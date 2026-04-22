package exceptions;

public class ImmutableFieldChanged extends ApplicationException {
    public ImmutableFieldChanged(String message) {
        super(message);
    }

    public ImmutableFieldChanged(Object... args) {
        super("immutable_field_changed", args);
    }
}
