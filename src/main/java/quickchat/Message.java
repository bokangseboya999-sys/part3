package quickchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/** Parts 2 and 3: a single chat message and the methods that act on it. */
public class Message {

    public static final String FLAG_SENT = "Sent";
    public static final String FLAG_STORED = "Stored";
    public static final String FLAG_DISREGARD = "Disregard";

    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+\\d{1,3}\\d{1,10}$");
    private static final Random RANDOM = new Random();

    private static final List<Message> SENT_LOG = new ArrayList<>();
    private static int totalSent = 0;

    private final String sender;
    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String text;
    private final String messageHash;
    private String flag = "";

    public Message(String sender, String recipient, String text, int messageNumber) {
        this(sender, recipient, text, messageNumber, generateMessageID());
    }


    public Message(String sender, String recipient, String text, int messageNumber, String messageID) {
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.messageNumber = messageNumber;
        this.messageID = messageID;
        this.messageHash = createMessageHash(messageID, messageNumber, text);
    }

    // ---------- ID ----------


    public static String generateMessageID() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }


    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // ---------- recipient ----------

    public boolean isRecipientValid() {
        return recipient != null && CELL_PATTERN.matcher(recipient).matches();
    }

    public String checkRecipientCell() {
        return isRecipientValid()
                ? "Cell phone number successfully captured."
                : "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    // ---------- message length ----------

    public boolean isLengthValid() {
        return text != null && text.length() <= 250;
    }

    public String checkMessageLength() {
        if (isLengthValid()) {
            return "Message ready to send.";
        }
        int over = text == null ? 0 : text.length() - 250;
        return "Message exceeds 250 characters by " + over + "; please reduce the size.";
    }

    // ---------- hash ----------

    public static String createMessageHash(String id, int messageNumber, String text) {
        String prefix = id.length() >= 2 ? id.substring(0, 2) : id;
        String[] words = text.trim().split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    public String createMessageHash() {
        return messageHash;
    }

    // ---------- send / store / disregard ----------

    /** Applies the user's choice: 1 = send, 2 = disregard, 3 = store. */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                flag = FLAG_SENT;
                SENT_LOG.add(this);
                totalSent++;
                return "Message successfully sent.";
            case 2:
                flag = FLAG_DISREGARD;
                return "Press 0 to delete the message.";
            case 3:
                flag = FLAG_STORED;
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }


    public String details() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text;
    }

    /** All messages sent while the program is running. */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : SENT_LOG) {
            sb.append(m.details()).append("\n\n");
        }
        return sb.toString().trim();
    }

    public static int returnTotalMessages() {
        return totalSent;
    }

    /** Test helper. */
    public static void resetCounters() {
        SENT_LOG.clear();
        totalSent = 0;
    }

    /** Appends this message to the JSON file. */
    public void storeMessage(String path) throws IOException {
        List<Message> existing = JsonStorage.read(path);
        existing.add(this);
        JsonStorage.write(path, existing);
    }

    // ---------- getters ----------

    public String getSender() { return sender; }
    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getText() { return text; }
    public String getMessageHash() { return messageHash; }
    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }
}
