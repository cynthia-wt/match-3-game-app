package engine;

import java.util.Set;

public class GameConfig {
    public static final char BLANK_CELL = '.';
    public static final int BRICK_LENGTH = 3;
    public static final int MAX_NUMBER_OF_BRICKS = 5;
    public static final Set<Character> ALLOWED_SYMBOLS = Set.of('~', '^', '*', '@');
    public static final int MAX_NUMBER_OF_COMMANDS = 2;
    public static final int MIN_MATCH_NUMBER = 3;
}
