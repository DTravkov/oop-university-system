package model.enumeration;

public enum AttestationType {
    FIRST_ATTESTATION("First attestation"),
    SECOND_ATTESTATION("Second attestation"),
    FINAL_EXAM("Final exam");

    private final String label;

    AttestationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
