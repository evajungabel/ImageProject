package hu.progmasters.moovsmart.exception;


public class RoleAdminExistsException extends RuntimeException {

    private final String username;

    public RoleAdminExistsException(String username) {
        super("ROLE_ADMIN exists with: " + username);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
