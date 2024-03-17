package hu.progmasters.moovsmart.exception;

public class EmailAddressExistsException extends RuntimeException {

    private final String email;

    public EmailAddressExistsException(String email) {
        super("Email address exists with: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
