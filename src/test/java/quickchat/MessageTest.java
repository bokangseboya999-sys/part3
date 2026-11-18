package quickchat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageTest {

    @BeforeEach
    void reset() {
        Message.resetCounters();
    }

    private Message testCase1() {
        return new Message("Sbu", "+27718693002", "Ekse Bk, can you join us for dinner tonight?", 0, "0012345678");
    }

    @Test
    void messageUnder250Characters() {
        assertEquals("Message ready to send.", testCase1().checkMessageLength());
    }

    @Test
    void messageOver250Characters() {
        String longText = "a".repeat(260);
        Message m = new Message("Sbu", "+27718693002", longText, 1);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.", m.checkMessageLength());
    }

    @Test
    void recipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.", testCase1().checkRecipientCell());
    }

    @Test
    void recipientIncorrectlyFormatted() {
        Message m = new Message("Sbu", "08575975889", "Ekse X, did you receive the payment?", 1);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.", m.checkRecipientCell());
    }

    @Test
    void messageHashIsCorrect() {
        assertEquals("00:0:HITONIGHT", testCase1().createMessageHash());
    }

    @Test
    void remainingHashesInLoop() {
        String[] texts = {"Bk, can you join us for dinner tonight?", "X, did you receive the payment?"};
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};
        for (int i = 0; i < texts.length; i++) {
            assertEquals(expected[i], Message.createMessageHash("0012345678", i, texts[i]));
        }
    }

    @Test
    void messageIDIsCreated() {
        Message m = new Message("Sbu", "+27718693002", "Hello there", 0);
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageID().length());
        assertTrue(m.getMessageID().matches("\\d{10}"));
    }

    @Test
    void sendMessageOptions() {
        assertEquals("Message successfully sent.", testCase1().sentMessage(1));
        assertEquals("Press 0 to delete the message.", testCase1().sentMessage(2));
        assertEquals("Message successfully stored.", testCase1().sentMessage(3));
    }

    @Test
    void totalMessagesSent() {
        testCase1().sentMessage(1);
        new Message("Sbu", "+27718693002", "X, did you receive the payment?", 1).sentMessage(2);
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void printMessagesListsSentOnly() {
        Message m = testCase1();
        m.sentMessage(1);
        String printed = Message.printMessages();
        assertTrue(printed.contains("Message ID: 0012345678"));
        assertTrue(printed.contains("Message Hash: 00:0:HITONIGHT"));
        assertTrue(printed.contains("Recipient: +27718693002"));
        assertTrue(printed.contains("Hi Bk, can you join us for dinner tonight?"));
    }
}
