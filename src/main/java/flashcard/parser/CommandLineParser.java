package flashcard.parser;

import java.util.List;

public class CommandLineParser {
    private final String cardsFile;
    private final String order;
    private final boolean invertCards;
    private final int repetitions;
    private final boolean helpOnly;

    public CommandLineParser(String[] args) {
        String file = null;
        String orderOpt = "random";
        boolean invert = false;
        int reps = 1;
        boolean help = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help" -> help = true;
                case "--order" -> {
                    if (i + 1 < args.length) {
                        orderOpt = args[++i];
                        if (!List.of("random", "worst-first", "recent-mistakes-first").contains(orderOpt)) {
                            throw new IllegalArgumentException("Invalid --order option: " + orderOpt);
                        }
                    } else throw new IllegalArgumentException("Missing value for --order");
                }
                case "--repetitions" -> {
                    if (i + 1 < args.length) {
                        reps = Integer.parseInt(args[++i]);
                    } else throw new IllegalArgumentException("Missing value for --repetitions");
                }
                case "--invertCards" -> invert = true;
                default -> {
                    if (!args[i].startsWith("--") && file == null) {
                        file = args[i];
                    }
                }
            }
        }

        this.cardsFile = file;
        this.order = orderOpt;
        this.invertCards = invert;
        this.repetitions = reps;
        this.helpOnly = help;
    }

    public String getCardsFile() { return cardsFile; }
    public String getOrder() { return order; }
    public boolean isInvertCards() { return invertCards; }
    public int getRepetitions() { return repetitions; }
    public boolean isHelpOnly() { return helpOnly; }
}
