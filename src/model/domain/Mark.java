package model.domain;

import model.enumeration.AttestationType;
import utils.FieldValidator;

import java.io.Serializable;
import java.util.Objects;

/**
 * Component holding the three attestation scores for an enrollment.
 */
public class Mark implements Serializable {

    private static final long serialVersionUID = 1L;

    private double firstAttestationPoint;
    private double secondAttestationPoint;
    private double finalExamPoint;

    Mark() {
        this.firstAttestationPoint = 0.0;
        this.secondAttestationPoint = 0.0;
        this.finalExamPoint = 0.0;
    }

    public double getFirstAttestationPoint() {
        return firstAttestationPoint;
    }

    public double getSecondAttestationPoint() {
        return secondAttestationPoint;
    }

    public double getFinalExamPoint() {
        return finalExamPoint;
    }

    public double getTotalPoint() {
        return firstAttestationPoint + secondAttestationPoint + finalExamPoint;
    }

    public double getGpa(){
        double gpa = (getTotalPoint() / 100.0) * 4.0;
        return Math.round(gpa * 100.0) / 100.0;
    }

    void addPoints(AttestationType attestationType, double delta) {
        switch (attestationType) {
            case FIRST_ATTESTATION:
                addToFirst(delta);
                break;
            case SECOND_ATTESTATION:
                addToSecond(delta);
                break;
            case FINAL_EXAM:
                addToFinal(delta);
                break;
        }
    }

    private void addToFirst(double delta) {
        double next = firstAttestationPoint + delta;
        FieldValidator.requireInRange(next, 0, 30, "First attestation point");
        this.firstAttestationPoint = next;
    }

    private void addToSecond(double delta) {
        double next = secondAttestationPoint + delta;
        FieldValidator.requireInRange(next, 0, 30, "Second attestation point");
        this.secondAttestationPoint = next;
    }

    private void addToFinal(double delta) {
        double next = finalExamPoint + delta;
        FieldValidator.requireInRange(next, 0, 40, "Final exam point");
        this.finalExamPoint = next;
    }

    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark = (Mark) o;
        return Double.compare(firstAttestationPoint, mark.firstAttestationPoint) == 0
                && Double.compare(secondAttestationPoint, mark.secondAttestationPoint) == 0
                && Double.compare(finalExamPoint, mark.finalExamPoint) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstAttestationPoint, secondAttestationPoint, finalExamPoint);
    }

    public String asLine() {
        return String.format("Total: %.1f | GPA: %.2f", getTotalPoint(), getGpa());
    }

    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("First attestation: ").append(firstAttestationPoint).append('\n');
        sb.append("Second attestation: ").append(secondAttestationPoint).append('\n');
        sb.append("Final exam: ").append(finalExamPoint).append('\n');
        sb.append("Total: ").append(getTotalPoint()).append('\n');
        sb.append("GPA: ").append(getGpa()).append('\n');
        return sb.toString();
    }

    @Override
    public String toString() {
        return "First Attestation: " + firstAttestationPoint +
               " | Second Attestation: " + secondAttestationPoint +
               " | Final exam: " + finalExamPoint;
    }
}
