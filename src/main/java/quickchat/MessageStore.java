package quickchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Part 3: the arrays that hold every message, plus the "Stored Messages" operations. */
public class MessageStore {

    // The five arrays required by the brief. No hard-coding: they are filled at run time.
    private final List<Message> sentMessages = new ArrayList<>();
    private final List<Message> disregardedMessages = new ArrayList<>();
    private final List<Message> storedMessages = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> messageIDs = new ArrayList<>();

    // ---------- populating the arrays ----------

    public void addSent(Message m) {
        sentMessages.add(m);
        register(m);
    }

    public void addDisregarded(Message m) {
        disregardedMessages.add(m);
        register(m);
    }

    public void addStored(Message m) {
        storedMessages.add(m);
        register(m);
    }

    private void register(Message m) {
        messageHashes.add(m.getMessageHash());
        messageIDs.add(m.getMessageID());
    }

    /** Reads the JSON file written by Message.storeMessage() into the stored-messages array. */
    public void loadStoredFromJson(String path) throws IOException {
        for (Message m : JsonStorage.read(path)) {
            addStored(m);
        }
    }

    // ---------- 2a. sender and recipient ----------

    public List<String> displaySenderAndRecipient() {
        List<String> out = new ArrayList<>();
        for (Message m : storedMessages) {
            out.add("Sender: " + m.getSender() + " | Recipient: " + m.getRecipient());
        }
        return out;
    }

    // ---------- 2b. longest message ----------

    /** Longest message held in the stored array; null if there are none. */
    public String longestStoredMessage() {
        String longest = null;
        for (Message m : storedMessages) {
            if (longest == null || m.getText().length() > longest.length()) {
                longest = m.getText();
            }
        }
        return longest;
    }

    // ---------- 2c. search by message ID ----------

    /** Searches every message held by the app; returns the message text, or null if not found. */
    public String searchByMessageID(String id) {
        int index = messageIDs.indexOf(id);
        return index < 0 ? null : findByHash(messageHashes.get(index)).getText();
    }

    /** Recipient and message for an ID, as required by 2c. */
    public String searchByMessageIDDetails(String id) {
        int index = messageIDs.indexOf(id);
        if (index < 0) {
            return "No message found with ID " + id + ".";
        }
        Message m = findByHash(messageHashes.get(index));
        return "Recipient: " + m.getRecipient() + " | Message: " + m.getText();
    }

    // ---------- 2d. search by recipient ----------

    public List<String> searchByRecipient(String recipient) {
        List<String> out = new ArrayList<>();
        for (Message m : allMessages()) {
            if (m.getRecipient().equals(recipient) && !disregardedMessages.contains(m)
                    && !m.getFlag().equals(Message.FLAG_DISREGARD)) {
                out.add(m.getText());
            }
        }
        return out;
    }

    // ---------- 2e. delete by hash ----------

    public String deleteByHash(String hash) {
        Message m = findByHash(hash);
        if (m == null) {
            return "No message found with hash " + hash + ".";
        }
        sentMessages.remove(m);
        storedMessages.remove(m);
        disregardedMessages.remove(m);
        int index = messageHashes.indexOf(hash);
        messageHashes.remove(index);
        messageIDs.remove(index);
        return "Message: \"" + m.getText() + "\" successfully deleted.";
    }

    /** Rewrites the JSON file so that deletions survive a restart. */
    public void saveStored(String path) throws IOException {
        JsonStorage.write(path, storedMessages);
    }

    // ---------- 2f. report ----------

    public String report() {
        StringBuilder sb = new StringBuilder("===== STORED MESSAGES REPORT =====\n");
        for (Message m : storedMessages) {
            sb.append("Message ID: ").append(m.getMessageID()).append("\n")
              .append("Message Hash: ").append(m.getMessageHash()).append("\n")
              .append("Sender: ").append(m.getSender()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getText()).append("\n")
              .append("-----------------------------------\n");
        }
        return sb.toString();
    }

    /** Report of all sent messages: hash, recipient, message. */
    public String sentReport() {
        StringBuilder sb = new StringBuilder("===== SENT MESSAGES REPORT =====\n");
        for (Message m : sentMessages) {
            sb.append("Message Hash: ").append(m.getMessageHash()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getText()).append("\n")
              .append("-----------------------------------\n");
        }
        return sb.toString();
    }

    // ---------- helpers / getters ----------

    private List<Message> allMessages() {
        List<Message> all = new ArrayList<>();
        // Preserve the order in which the messages were captured (hash array is in insertion order).
        for (String h : messageHashes) {
            all.add(findByHash(h));
        }
        return all;
    }

    private Message findByHash(String hash) {
        for (List<Message> list : List.of(sentMessages, storedMessages, disregardedMessages)) {
            for (Message m : list) {
                if (m.getMessageHash().equals(hash)) {
                    return m;
                }
            }
        }
        return null;
    }

    public List<String> getSentMessageTexts() {
        List<String> out = new ArrayList<>();
        for (Message m : sentMessages) {
            out.add(m.getText());
        }
        return out;
    }

    public List<Message> getSentMessages() { return sentMessages; }
    public List<Message> getDisregardedMessages() { return disregardedMessages; }
    public List<Message> getStoredMessages() { return storedMessages; }
    public List<String> getMessageHashes() { return messageHashes; }
    public List<String> getMessageIDs() { return messageIDs; }
}
