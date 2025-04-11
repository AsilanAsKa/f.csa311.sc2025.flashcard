package flashcard.stats;

import java.util.List;

import flashcard.model.Card;

public class AchievementTracker {
    private final List<Card> cards;

    public AchievementTracker(List<Card> cards) {
        this.cards = cards;
    }

    public boolean achievedCorrect() {
        return cards.stream().allMatch(c -> c.getIncorrectCount() == 0 && c.getCorrectCount() > 0);
    }

    public boolean achievedRepeat() {
        return cards.stream().anyMatch(c -> c.getTotalAttempts() > 5);
    }

    public boolean achievedConfident() {
        return cards.stream().anyMatch(c -> c.getCorrectCount() >= 3);
    }

    public void printAchievements() {
        System.out.println("Achievements Unlocked:");
        boolean any = false;

        if (achievedCorrect()) {
            System.out.println(" CORRECT - All cards answered correctly in final round.");
            any = true;
        }
        if (achievedRepeat()) {
            System.out.println("  REPEAT - At least one card answered more than 5 times.");
            any = true;
        }
        if (achievedConfident()) {
            System.out.println("  CONFIDENT - At least one card answered correctly 3+ times.");
            any = true;
        }

        if (!any) {
            System.out.println("  No achievements this time. Try again!");
        }
    }
}
