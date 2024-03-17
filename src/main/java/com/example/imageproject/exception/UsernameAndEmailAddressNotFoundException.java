package hu.progmasters.moovsmart.exception;

public class UsernameAndEmailAddressNotFoundException extends RuntimeException{

    private final String username;
    private final String email;

    public UsernameAndEmailAddressNotFoundException(String username, String email) {
        super("Username or email was not found with: " + username + email);
        this.username = username;
        this.email = email;
    }
}
