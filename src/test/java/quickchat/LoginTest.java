package quickchat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login("Kyle", "Smith");
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
    }

    // ---------- assertEquals ----------

    @Test
    void usernameCorrectlyFormatted() {
        assertEquals("Username successfully captured.", login.usernameMessage("kyl_1"));
    }

    @Test
    void usernameIncorrectlyFormatted() {
        assertEquals("Username is not correctly formatted; please ensure that your username contains "
                + "an underscore and is no more than five characters in length.",
                login.usernameMessage("kyle!!!!!!!"));
    }

    @Test
    void passwordMeetsComplexity() {
        assertEquals("Password successfully captured.", login.passwordMessage("Ch&&sec@ke99!"));
    }

    @Test
    void passwordFailsComplexity() {
        assertEquals("Password is not correctly formatted; please ensure that the password contains "
                + "at least eight characters, a capital letter, a number, and a special character.",
                login.passwordMessage("password"));
    }

    @Test
    void cellPhoneCorrectlyFormatted() {
        assertEquals("Cell number successfully captured.", login.cellMessage("+27838968976"));
    }

    @Test
    void cellPhoneIncorrectlyFormatted() {
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; "
                + "please correct the number and try again.", login.cellMessage("08966553"));
    }

    @Test
    void loginStatusMessages() {
        assertEquals("Welcome Kyle, Smith it is great to see you again.", login.returnLoginStatus(true));
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus(false));
    }

    // ---------- assertTrue / assertFalse ----------

    @Test
    void loginSuccessful() {
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void loginFailed() {
        assertFalse(login.loginUser("kyl_1", "wrongPass1!"));
        assertFalse(login.loginUser("nope_", "Ch&&sec@ke99!"));
    }

    @Test
    void usernameBooleans() {
        assertTrue(login.checkUserName("kyl_1"));
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    void passwordBooleans() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    void cellBooleans() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }
}
