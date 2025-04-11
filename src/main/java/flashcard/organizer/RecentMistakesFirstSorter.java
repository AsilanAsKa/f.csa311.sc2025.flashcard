package flashcard.organizer;

import java.util.List;

import flashcard.model.Card;

public class RecentMistakesFirstSorter implements CardOrganizer {
    @Override
    public List<Card> organize(List<Card> cards) {
        cards.sort((a, b) -> Long.compare(b.getLastMistakeTime(), a.getLastMistakeTime()));
        return cards;
    }
}