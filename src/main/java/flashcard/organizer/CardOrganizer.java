package flashcard.organizer;

import flashcard.model.Card;
import java.util.List;

public interface CardOrganizer {
    List<Card> organize(List<Card> cards);
}
