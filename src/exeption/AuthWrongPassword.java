package exeption;

public class AuthWrongPassword extends AuthFailException {
    static final String error = "ERROR 101"; // error number
    static final private String errorDescriptionStart = "Can't find in database string with "; // description of error for info
    static final private String errorDescriptionEnd = " username and password. Maybe mismatch?"; // description of error for info
    private String username; // what username wrote client?
    private String password; // what password entered client?

    public AuthWrongPassword(String user, String pass) {
        super();
        this.username = user;
        this.password = pass;
    }

    public String getFailInfo() {

        return this.error + ": " + errorDescriptionStart + this.username + ":" + this.password + errorDescriptionEnd;
    }
}
