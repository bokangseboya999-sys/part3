package quickchat;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Part 3 unit tests using the supplied test data (messages 1-5). */
class MessageStoreTest {

    private MessageStore store;
    private Message m1, m2, m3, m4, m5;

    @BeforeEach
    void setUp() {
        store = new MessageStore();
        m1 = new Message("Dev", "+27834557896", "Did you get the cake?", 0, "1000000001");
        m2 = new Message("Dev", "+27838884567", "Where are you? You are late! I have asked you to be on time.", 1, "1000000002");
        m3 = new Message("Dev", "+27834484567", "Yohoooo, I am at your gate.", 2, "1000000003");
        // Test data message 4 supplies the developer number 0838884567; it is used as the message ID here.
        m4 = new Message("Dev", "Developer", "It is dinner time!", 3, "0838884567");
        m5 = new Message("Dev", "+27838884567", "Ok, I am leaving without you.", 4, "1000000005");

        m1.setFlag(Message.FLAG_SENT);       store.addSent(m1);
        m2.setFlag(Message.FLAG_STORED);     store.addStored(m2);
        m3.setFlag(Message.FLAG_DISREGARD);  store.addDisregarded(m3);
        m4.setFlag(Message.FLAG_SENT);       store.addSent(m4);
        m5.setFlag(Message.FLAG_STORED);     store.addStored(m5);
    }

    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of("Did you get the cake?", "It is dinner time!"), store.getSentMessageTexts());
    }

    @Test
    void displayLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                store.longestStoredMessage());
    }

    @Test
    void searchForMessageID() {
        assertEquals("It is dinner time!", store.searchByMessageID("0838884567"));
    }

    @Test
    void searchMessagesForRecipient() {
        assertEquals(List.of("Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."), store.searchByRecipient("+27838884567"));
    }

    @Test
    void deleteMessageUsingHash() {
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                store.deleteByHash(m2.getMessageHash()));
        assertEquals(1, store.getStoredMessages().size());
    }

    @Test
    void deleteUnknownHash() {
        assertTrue(store.deleteByHash("NOPE").startsWith("No message found"));
    }

    @Test
    void displayReport() {
        String report = store.report();
        assertTrue(report.contains(m2.getMessageHash()));
        assertTrue(report.contains("+27838884567"));
        assertTrue(report.contains("Ok, I am leaving without you."));
    }

    @Test
    void arraysAreParallel() {
        assertEquals(5, store.getMessageHashes().size());
        assertEquals(5, store.getMessageIDs().size());
    }

    @Test
    void jsonRoundTrip(@TempDir Path dir) throws IOException {
        String file = dir.resolve("stored.json").toString();
        m2.storeMessage(file);
        m5.storeMessage(file);

        MessageStore loaded = new MessageStore();
        loaded.loadStoredFromJson(file);
        assertEquals(2, loaded.getStoredMessages().size());
        assertEquals(m2.getText(), loaded.getStoredMessages().get(0).getText());
        assertEquals(m2.getMessageHash(), loaded.getStoredMessages().get(0).getMessageHash());
        assertTrue(Files.exists(Path.of(file)));
    }
}
