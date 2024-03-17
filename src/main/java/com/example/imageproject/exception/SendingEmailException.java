package hu.progmasters.moovsmart.exception;

public class SendingEmailException extends RuntimeException{

    private final String email;

    public SendingEmailException(String email) {
        this.email = email;
    }
}
