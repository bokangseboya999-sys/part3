# QuickChat

Console app (Java 17, Maven, JUnit 5) covering Parts 1-3 of the Programming 1 POE.

## Run
    mvn test                                   # unit tests
    mvn compile exec:java -Dexec.mainClass=quickchat.QuickChat   # or run QuickChat.main in your IDE

## Structure
- `Login` - registration/login validation (Part 1)
- `Message` - message ID, hash, validation, send/store/disregard (Part 2)
- `MessageStore` - arrays + stored-message operations (Part 3)
- `JsonStorage` - JSON read/write for stored messages
- `.github/workflows/maven.yml` - GitHub Actions CI that runs the tests on every push
