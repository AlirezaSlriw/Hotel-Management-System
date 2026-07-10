package hms.exceptions;

public class InsufficientPaymentException extends Exception {
    public InsufficientPaymentException(String message) {
        super(message);
    }
}
