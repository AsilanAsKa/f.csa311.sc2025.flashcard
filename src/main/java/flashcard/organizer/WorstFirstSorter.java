package flashcard.organizer;

import java.util.List;

import flashcard.model.Card;

public class WorstFirstSorter implements CardOrganizer {
    @Override
    public List<Card> organize(List<Card> cards) {
        cards.sort((a, b) -> Integer.compare(b.getIncorrectCount(), a.getIncorrectCount()));
        return cards;
    }
}