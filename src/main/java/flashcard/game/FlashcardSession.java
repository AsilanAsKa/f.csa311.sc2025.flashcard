package flashcard.game;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import flashcard.model.Card;
import flashcard.organizer.CardOrganizer;
import flashcard.organizer.RandomSorter;
import flashcard.organizer.RecentMistakesFirstSorter;
import flashcard.organizer.WorstFirstSorter;
import flashcard.parser.CommandLineParser;
import flashcard.stats.AchievementTracker;

public class FlashcardSession {
    private final List<Card> cards;
    private final int repetitions;
    private final String order;
    private final Scanner scanner = new Scanner(System.in);
    private static final String STATS_FILE = "card_stats.ser";

    public FlashcardSession(CommandLineParser parser) throws Exception {
        this.cards = loadCardsFromFile(parser.getCardsFile(), parser.isInvertCards());
        this.repetitions = parser.getRepetitions();
        this.order = parser.getOrder();
        loadStats();
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

    private void loadStats() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STATS_FILE))) {
            List<Card> savedStats = (List<Card>) ois.readObject();
            for (Card loaded : savedStats) {
                for (Card c : cards) {
                    if (c.equals(loaded)) {
                        c.mergeStatsFrom(loaded);
                    }
                }
            }
        } catch (Exception ignored) {}
    }
    
    private void saveStats() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATS_FILE))) {
            oos.writeObject(cards);
        } catch (Exception ignored) {
        }
    }

    private boolean askRetry() {
        System.out.print("\nDo you want to try again? (yes/no): ");
        String again = scanner.nextLine().trim().toLowerCase();
        return again.equals("yes") || again.equals("y");
    }

    public void startSession() {
        Map<Card, Integer> correctTracker = new HashMap<>();
        List<Long> answerTimes = new ArrayList<>();

        for (Card card : cards) correctTracker.put(card, 0);

        System.out.println("\n Flashcard session started! Type 'exit' to stop.\n");

        boolean allComplete;
        do {
            allComplete = true;

            CardOrganizer organizer = getOrganizer(order);
            List<Card> orderedCards = organizer.organize(new ArrayList<>(cards));

            for (Card card : orderedCards) {
                if (correctTracker.get(card) >= repetitions) continue;

                System.out.println("Q: " + card.getQuestion());
                System.out.print("Your answer: ");

                long startTime = System.currentTimeMillis();
                String input = scanner.nextLine().trim();
                long duration = System.currentTimeMillis() - startTime;
                answerTimes.add(duration);

                if (input.equalsIgnoreCase("exit")) {
                    saveStats();
                    return;
                }

                boolean correct = input.equalsIgnoreCase(card.getAnswer());
                card.recordAnswer(correct);

                if (correct) {
                    System.out.println(" Correct!");
                    correctTracker.put(card, correctTracker.get(card) + 1);
                } else {
                    System.out.println(" Wrong! Correct answer was: " + card.getAnswer());
                }

                if (correctTracker.get(card) < repetitions) allComplete = false;
                System.out.println();
            }

            System.out.println(" Session progress:");
            for (Card card : cards) {
                System.out.println("  - " + card.getQuestion() +
                        " | Mistakes: " + card.getIncorrectCount() +
                        " | Last mistake: " + card.getLastMistakeTime());
            }

        } while (!allComplete);

        System.out.println("Session complete! All cards answered correctly " + repetitions + " times.");
        new AchievementTracker(cards).printAchievements();

        long totalTime = answerTimes.stream().mapToLong(Long::longValue).sum();
        double avgSeconds = totalTime / 1000.0 / answerTimes.size();
        System.out.printf(" Average response time: %.2f seconds\n", avgSeconds);

        if (avgSeconds < 10.0) {
            System.out.println(" FAST - You answered quickly with an average under 5 seconds!");
        }

        saveStats();

        if (askRetry()) {
            System.out.println("\n Restarting the session...\n");
            startSession();
        } else {
            System.out.println(" Thank you for using Flashcard! Goodbye!");
        }
    }

    private CardOrganizer getOrganizer(String order) {
        return switch (order) {
            case "worst-first" -> new WorstFirstSorter();
            case "recent-mistakes-first" -> new RecentMistakesFirstSorter();
            default -> new RandomSorter();
        };
    }
}