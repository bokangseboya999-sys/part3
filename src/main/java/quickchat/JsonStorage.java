package quickchat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal dependency-free JSON reader/writer for stored messages.
 * (You may swap this for Gson or org.json if your lecturer prefers a library; the
 * calling code only uses read() and write().)
 */
public final class JsonStorage {

    private static final Pattern OBJECT = Pattern.compile("\\{([^{}]*)\\}", Pattern.DOTALL);
    private static final Pattern FIELD = Pattern.compile("\"(\\w+)\"\\s*:\\s*(\"(?:[^\"\\\\]|\\\\.)*\"|-?\\d+)");

    private JsonStorage() { }

    public static void write(String path, List<Message> messages) throws IOException {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            sb.append("  {\n")
              .append("    \"sender\": ").append(quote(m.getSender())).append(",\n")
              .append("    \"messageID\": ").append(quote(m.getMessageID())).append(",\n")
              .append("    \"messageNumber\": ").append(m.getMessageNumber()).append(",\n")
              .append("    \"messageHash\": ").append(quote(m.getMessageHash())).append(",\n")
              .append("    \"recipient\": ").append(quote(m.getRecipient())).append(",\n")
              .append("    \"message\": ").append(quote(m.getText())).append("\n")
              .append("  }").append(i < messages.size() - 1 ? "," : "").append("\n");
        }
        sb.append("]\n");
        Files.write(Paths.get(path), sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /** Returns an empty list if the file does not exist yet. */
    public static List<Message> read(String path) throws IOException {
        List<Message> result = new ArrayList<>();
        Path p = Paths.get(path);
        if (!Files.exists(p)) {
            return result;
        }
        String json = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        Matcher objects = OBJECT.matcher(json);
        while (objects.find()) {
            String sender = "", id = "", recipient = "", text = "";
            int number = 0;
            Matcher f = FIELD.matcher(objects.group(1));
            while (f.find()) {
                String key = f.group(1);
                String val = f.group(2);
                switch (key) {
                    case "sender": sender = unquote(val); break;
                    case "messageID": id = unquote(val); break;
                    case "messageNumber": number = Integer.parseInt(val); break;
                    case "recipient": recipient = unquote(val); break;
                    case "message": text = unquote(val); break;
                    default: break; // messageHash is recomputed from ID, number and text
                }
            }
            Message m = new Message(sender, recipient, text, number, id);
            m.setFlag(Message.FLAG_STORED);
            result.add(m);
        }
        return result;
    }

    private static String quote(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

    private static String unquote(String s) {
        String body = s.substring(1, s.length() - 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (c == '\\' && i + 1 < body.length()) {
                char n = body.charAt(++i);
                switch (n) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    default: sb.append(n);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
