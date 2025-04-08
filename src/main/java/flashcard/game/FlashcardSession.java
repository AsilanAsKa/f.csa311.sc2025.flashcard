package flashcard.game;

import flashcard.model.Card;
import flashcard.organizer.*;
import flashcard.parser.CommandLineParser;
import flashcard.stats.AchievementTracker;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class FlashcardSession {
    private final List<Card> cards;
    private final boolean invert;
    private final int repetitions;
    private final String order;

    public FlashcardSession(CommandLineParser parser) throws Exception {
        this.cards = loadCardsFromFile(parser.getCardsFile(), parser.isInvertCards());
        this.invert = parser.isInvertCards();
        this.repetitions = parser.getRepetitions();
        this.order = parser.getOrder();
    }

    private List<Card> loadCardsFromFile(String fileName, boolean invertCards) throws Exception {
        List<Card> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("::");
                if (parts.length != 2) continue;
                String q = invertCards ? parts[1] : parts[0];
                String a = invertCards ? parts[0] : parts[1];
                result.add(new Card(q, a));
            }
        }
        return result;
    }

    private CardOrganizer getOrganizer(String order) {
        return switch (order) {
            case "worst-first" -> new WorstFirstSorter();
            case "recent-mistakes-first" -> new RecentMistakesFirstSorter();
            default -> new RandomSorter();
        };
    }

    public void startSession() {
        Scanner scanner = new Scanner(System.in);
        Map<Card, Integer> correctTracker = new HashMap<>();
        List<Long> answerTimes = new ArrayList<>();

        for (Card card : cards) correctTracker.put(card, 0);

        System.out.println("\n🔁 Flashcard session started! Type 'exit' to stop.\n");

        boolean allComplete;
        do {
            allComplete = true;

            CardOrganizer organizer = getOrganizer(order);
            organizer.organize(cards);

            for (Card card : cards) {
                if (correctTracker.get(card) >= repetitions) continue;

                System.out.println("Q: " + card.getQuestion());
                System.out.print("Your answer: ");

                long startTime = System.currentTimeMillis(); // start timing
                String input = scanner.nextLine().trim();
                long duration = System.currentTimeMillis() - startTime; // end timing
                answerTimes.add(duration);

                if (input.equalsIgnoreCase("exit")) return;

                boolean correct = input.equalsIgnoreCase(card.getAnswer());
                card.recordAnswer(correct);

                if (correct) {
                    System.out.println("✅ Correct!");
                    correctTracker.put(card, correctTracker.get(card) + 1);
                } else {
                    System.out.println("❌ Wrong! Correct answer was: " + card.getAnswer());
                }

                if (correctTracker.get(card) < repetitions) allComplete = false;

                System.out.println();
            }

            // Optional: View progress after each round
            System.out.println("📊 Session progress:");
            for (Card card : cards) {
                System.out.println("  - " + card.getQuestion() +
                        " | Mistakes: " + card.getIncorrectCount() +
                        " | Last mistake: " + card.getLastMistakeTime());
            }

        } while (!allComplete);

        System.out.println("🎉 Session complete! All cards answered correctly " + repetitions + " times.");

        new AchievementTracker(cards).printAchievements();

        // ⏱ Average response time calculation
        long totalTime = answerTimes.stream().mapToLong(Long::longValue).sum();
        double avgSeconds = totalTime / 1000.0 / answerTimes.size();
        System.out.printf("⏱ Average response time: %.2f seconds\n", avgSeconds);

        if (avgSeconds < 10.0) {
            System.out.println("🏅 FAST - You answered quickly with an average under 5 seconds!");
        }

        // YES/NO retry option
        System.out.print("\nDo you want to try again? (yes/no): ");
        String again = scanner.nextLine().trim().toLowerCase();

        if (again.equals("yes") || again.equals("y")) {
            System.out.println("\n🔄 Restarting the session...\n");
            startSession();
        } else {
            System.out.println("👋 Thank you for using Flashcard! Goodbye!");
        }
    }
}
