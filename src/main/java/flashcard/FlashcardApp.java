package flashcard;

import flashcard.game.FlashcardSession;
import flashcard.parser.CommandLineParser;

public class FlashcardApp {
    public static void main(String[] args) {
        try {
            CommandLineParser parser = new CommandLineParser(args);

            if (parser.isHelpOnly()) {
                System.out.println("Flashcard CLI Usage:");
                System.out.println("  flashcard <cards-file> [options]");
                System.out.println("Options:");
                System.out.println("  --help                 Show help info");
                System.out.println("  --order <type>         Order: random | worst-first | recent-mistakes-first");
                System.out.println("  --repetitions <num>    Repetition requirement per card");
                System.out.println("  --invertCards          Invert Q/A display");
                return;
            }

            if (parser.getCardsFile() == null) {
                System.out.println("Error: Cards file not specified.");
                return;
            }

            FlashcardSession session = new FlashcardSession(parser);
            session.startSession();

        } catch (Exception e) {
            System.out.println("Error parsing command-line: " + e.getMessage());
        }
    }
}
