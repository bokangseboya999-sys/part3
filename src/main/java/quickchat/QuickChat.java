package quickchat;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/** Entry point: registration, login, then the QuickChat menu (Parts 1-3). */
public class QuickChat {

    private static final String JSON_FILE = "storedMessages.json";

    private final Scanner in = new Scanner(System.in);
    private final MessageStore store = new MessageStore();
    private Login login;
    private int messagesCaptured = 0;
    private int maxMessages = 0;

    public static void main(String[] args) {
        new QuickChat().run();
    }

    private void run() {
        register();
        if (!doLogin()) {
            return;
        }
        System.out.println("Welcome to QuickChat.");

        try {
            store.loadStoredFromJson(JSON_FILE);   // stored messages from earlier runs
        } catch (IOException e) {
            System.out.println("Could not read stored messages: " + e.getMessage());
        }

        maxMessages = readInt("How many messages do you wish to enter? ");

        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages\n2) Show recently sent messages\n3) Quit\n4) Stored Messages");
            switch (readInt("Choose an option: ")) {
                case 1: sendMessages(); break;
                case 2: System.out.println("Coming Soon."); break;
                case 3: running = false; break;
                case 4: storedMessagesMenu(); break;
                default: System.out.println("Invalid option, please try again.");
            }
        }
        System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
    }

    // ---------- Part 1 ----------

    private void register() {
        System.out.println("=== Register ===");
        System.out.print("First name: ");
        String first = in.nextLine().trim();
        System.out.print("Last name: ");
        String last = in.nextLine().trim();
        login = new Login(first, last);

        while (true) {
            System.out.print("Username: ");
            String user = in.nextLine().trim();
            System.out.println(login.usernameMessage(user));
            System.out.print("Password: ");
            String pass = in.nextLine().trim();
            System.out.println(login.passwordMessage(pass));
            System.out.print("Cell number (e.g. +27838968976): ");
            String cell = in.nextLine().trim();
            System.out.println(login.cellMessage(cell));

            String result = login.registerUser(user, pass, cell);
            if (result.contains("registered successfully")) {
                System.out.println("User has been registered successfully.");
                return;
            }
            System.out.println("Please try again.\n");
        }
    }

    private boolean doLogin() {
        System.out.println("\n=== Login ===");
        System.out.print("Username: ");
        String user = in.nextLine().trim();
        System.out.print("Password: ");
        String pass = in.nextLine().trim();
        boolean ok = login.loginUser(user, pass);
        System.out.println(login.returnLoginStatus(ok));
        return ok;   // messages may only be sent after a successful login
    }

    // ---------- Part 2 ----------

    private void sendMessages() {
        while (messagesCaptured < maxMessages) {
            System.out.println("\nMessage " + (messagesCaptured + 1) + " of " + maxMessages);

            String recipient;
            while (true) {
                System.out.print("Recipient cell number: ");
                recipient = in.nextLine().trim();
                Message probe = new Message(login.getUsername(), recipient, "x", 0);
                System.out.println(probe.checkRecipientCell());
                if (probe.isRecipientValid()) break;
            }

            String text;
            while (true) {
                System.out.print("Message (max 250 characters): ");
                text = in.nextLine();
                Message probe = new Message(login.getUsername(), recipient, text.isBlank() ? "x" : text, 0);
                if (text.isBlank()) {
                    System.out.println("Message cannot be empty.");
                    continue;
                }
                System.out.println(probe.checkMessageLength());
                if (probe.isLengthValid()) break;
            }

            Message m = new Message(login.getUsername(), recipient, text, messagesCaptured);
            messagesCaptured++;

            System.out.println("\n1) Send Message\n2) Disregard Message\n3) Store Message to send later");
            int choice = readInt("Choose an option: ");
            System.out.println(m.sentMessage(choice));

            switch (choice) {
                case 1:
                    store.addSent(m);
                    System.out.println("\n" + m.details());
                    break;
                case 2:
                    if (readInt("") == 0) {
                        store.addDisregarded(m);
                        System.out.println("Message deleted.");
                    }
                    break;
                case 3:
                    try {
                        m.storeMessage(JSON_FILE);
                        store.addStored(m);
                    } catch (IOException e) {
                        System.out.println("Could not store the message: " + e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
        if (messagesCaptured >= maxMessages) {
            System.out.println("\nYou have entered all " + maxMessages + " messages.");
            System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
        }
    }

    // ---------- Part 3 ----------

    private void storedMessagesMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages ---"
                    + "\na) Display sender and recipient of all stored messages"
                    + "\nb) Display the longest stored message"
                    + "\nc) Search for a message ID"
                    + "\nd) Search for all messages stored for a recipient"
                    + "\ne) Delete a message using the message hash"
                    + "\nf) Display report of all stored messages"
                    + "\ng) Back");
            System.out.print("Choose: ");
            String c = in.nextLine().trim().toLowerCase();
            switch (c) {
                case "a":
                    store.displaySenderAndRecipient().forEach(System.out::println);
                    break;
                case "b":
                    String longest = store.longestStoredMessage();
                    System.out.println(longest == null ? "No stored messages." : "Longest message: " + longest);
                    break;
                case "c":
                    System.out.print("Message ID: ");
                    System.out.println(store.searchByMessageIDDetails(in.nextLine().trim()));
                    break;
                case "d":
                    System.out.print("Recipient: ");
                    List<String> found = store.searchByRecipient(in.nextLine().trim());
                    if (found.isEmpty()) System.out.println("No messages found.");
                    else found.forEach(System.out::println);
                    break;
                case "e":
                    System.out.print("Message hash: ");
                    System.out.println(store.deleteByHash(in.nextLine().trim().toUpperCase()));
                    try {
                        store.saveStored(JSON_FILE);
                    } catch (IOException e) {
                        System.out.println("Could not update the file: " + e.getMessage());
                    }
                    break;
                case "f":
                    System.out.println(store.report());
                    break;
                case "g":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private int readInt(String prompt) {
        while (true) {
            if (!prompt.isEmpty()) System.out.print(prompt);
            try {
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                prompt = "Try again: ";
            }
        }
    }
}
