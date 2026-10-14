package model;

public enum Alignment {
    HORIZONTAL,
    VERTICAL;

    public static Alignment fromPrefix(char prefix) {
        return switch (prefix) {
            case 'H' -> HORIZONTAL;
            case 'V' -> VERTICAL;
            default -> null;
        };
    }
}
