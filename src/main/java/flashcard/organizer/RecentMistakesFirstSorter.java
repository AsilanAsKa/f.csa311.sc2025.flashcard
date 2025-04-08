package flashcard.organizer;

import flashcard.model.Card;
import java.util.Comparator;
import java.util.List;

public class RecentMistakesFirstSorter implements CardOrganizer {
    @Override
    public List<Card> organize(List<Card> cards) {
        cards.sort(Comparator.comparingLong(Card::getLastMistakeTime).reversed());
        return cards;
    }
}
