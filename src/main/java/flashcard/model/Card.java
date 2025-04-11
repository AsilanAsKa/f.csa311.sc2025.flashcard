package flashcard.model;

import java.io.Serializable;

public class Card implements Serializable {
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

    public void mergeStatsFrom(Card other) {
        this.correctCount += other.correctCount;
        this.incorrectCount += other.incorrectCount;
        this.totalAttempts += other.totalAttempts;
        this.lastMistakeTime = Math.max(this.lastMistakeTime, other.lastMistakeTime);
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
} 
