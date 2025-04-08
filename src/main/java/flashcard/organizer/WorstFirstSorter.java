package flashcard.organizer;

import flashcard.model.Card;
import java.util.Comparator;
import java.util.List;

public class WorstFirstSorter implements CardOrganizer {
    @Override
    public List<Card> organize(List<Card> cards) {
        cards.sort(Comparator.comparingInt(Card::getIncorrectCount).reversed());
        return cards;
    }
}
