package flashcard.model;

public class Card {
    private final String question;
    private final String answer;
    private int correctCount = 0;
    private int incorrectCount = 0;
    private int totalAttempts = 0;
    private long lastMistakeTime = 0;

    public Card(String question, String answer) {
        this.question = question.trim();
        this.answer = answer.trim();
    }

    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }

    public void recordAnswer(boolean correct) {
        totalAttempts++;
        if (correct) correctCount++;
        else {
            incorrectCount++;
            lastMistakeTime = System.currentTimeMillis();
        }
    }

    public int getCorrectCount() { return correctCount; }
    public int getIncorrectCount() { return incorrectCount; }
    public int getTotalAttempts() { return totalAttempts; }
    public long getLastMistakeTime() { return lastMistakeTime; }

    @Override
public String toString() {
    return question + " | Mistakes: " + incorrectCount + " | LastMistakeTime: " + lastMistakeTime;
}
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card)) return false;
        Card other = (Card) obj;
        return question.equals(other.question) && answer.equals(other.answer);
    }

    @Override
    public int hashCode() {
        return 31 * question.hashCode() + answer.hashCode();
    }
    public int compareTo(Card other) {
        if (this.incorrectCount != other.incorrectCount) {
            return Integer.compare(this.incorrectCount, other.incorrectCount);
        }
        return Long.compare(this.lastMistakeTime, other.lastMistakeTime);
    }
    public int compare(Card other) {
        return Integer.compare(this.incorrectCount, other.incorrectCount);
    }
    public void resetStats() {
        correctCount = 0;
        incorrectCount = 0;
        totalAttempts = 0;
        lastMistakeTime = 0;
    }
    
}
