package hu.progmasters.moovsmart.exception;

public class PasswordNotValidException extends RuntimeException{

    private final String password;

    public PasswordNotValidException(String password) {
        super("Password is not valid with: " + password);
        this.password = password;
    }
}
