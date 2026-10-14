package model;

public enum Command {
    LEFT,
    RIGHT,
    DOWN;

    public static Command fromPrefix(char prefix) {
        return switch (prefix) {
            case 'L' -> LEFT;
            case 'R' -> RIGHT;
            case 'D' -> DOWN;
            default -> null;
        };
    }
}
