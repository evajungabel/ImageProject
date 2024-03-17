package hu.progmasters.moovsmart.exception;


public class CustomUserEmailNotFoundException extends RuntimeException {

    private final String email;

    public CustomUserEmailNotFoundException(String email) {
        super("CustomUserEmail was not found with: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
