package quickchat;

import java.util.regex.Pattern;

/**
 * Part 1: registration and login.
 *
 * Reference (regular expressions): Oracle, "java.util.regex.Pattern" Javadoc,
 * https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
 * (Replace/extend this with the exact sources you used when researching your regex.)
 */
public class Login {

    // "+" then an international code (1-3 digits) then a number of no more than 10 digits.
    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+\\d{1,3}\\d{1,10}$");

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellNumber;

    public Login() { }

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // ---------- validation methods ----------

    /** Username must contain an underscore and be no more than five characters long. */
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    /** Password: >= 8 chars, a capital letter, a number and a special character. */
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean capital = false, number = false, special = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                capital = true;
            } else if (Character.isDigit(c)) {
                number = true;
            } else if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                special = true;
            }
        }
        return capital && number && special;
    }

    /** Cell number must contain the international code and be correctly sized. */
    public boolean checkCellPhoneNumber(String cell) {
        return cell != null && CELL_PATTERN.matcher(cell).matches();
    }

    // ---------- message helpers ----------

    public String usernameMessage(String username) {
        return checkUserName(username)
                ? "Username successfully captured."
                : "Username is not correctly formatted; please ensure that your username "
                + "contains an underscore and is no more than five characters in length.";
    }

    public String passwordMessage(String password) {
        return checkPasswordComplexity(password)
                ? "Password successfully captured."
                : "Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, and a special character.";
    }

    public String cellMessage(String cell) {
        return checkCellPhoneNumber(cell)
                ? "Cell number successfully captured."
                : "Cell number is incorrectly formatted or does not contain an international code; "
                + "please correct the number and try again.";
    }

    // ---------- registration / login ----------

    /**
     * Validates the details in order (username, password, cell number). Returns the message for the first
     * failing check, or a success message after storing the details.
     */
    public String registerUser(String username, String password, String cell) {
        if (!checkUserName(username)) {
            return usernameMessage(username);
        }
        if (!checkPasswordComplexity(password)) {
            return passwordMessage(password);
        }
        if (!checkCellPhoneNumber(cell)) {
            return cellMessage(cell);
        }
        this.username = username;
        this.password = password;
        this.cellNumber = cell;
        return "Username successfully captured.\nPassword successfully captured.\n"
                + "Cell number successfully captured.\nUser has been registered successfully.";
    }

    /** True only if the entered details match those stored at registration. */
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return username != null && password != null
                && username.equals(enteredUsername) && password.equals(enteredPassword);
    }

    public String returnLoginStatus(boolean loggedIn) {
        return loggedIn
                ? "Welcome " + firstName + ", " + lastName + " it is great to see you again."
                : "Username or password incorrect, please try again.";
    }

    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
