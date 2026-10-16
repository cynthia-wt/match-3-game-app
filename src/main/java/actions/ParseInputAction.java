package actions;

import engine.GameConfig;
import model.Alignment;
import model.Brick;
import model.Command;
import model.InitializationInput;

import java.util.ArrayList;
import java.util.List;

public class ParseInputAction {

    // Sample input : 5 8 H^^* V*@^
    public static InitializationInput parseInitializationInput(String input) {
        String[] inputParts = input.split(" ");

        if (inputParts.length < 3) {
            throw new IllegalArgumentException("Enter width, height and at least one brick.");
        }
        if (inputParts.length > 2 + GameConfig.MAX_NUMBER_OF_BRICKS) {
            throw new IllegalArgumentException("A maximum of " + GameConfig.MAX_NUMBER_OF_BRICKS + " bricks is allowed.");
        }

        int width;
        int height;
        try {
            width = Integer.parseInt(inputParts[0]);
            height = Integer.parseInt(inputParts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Width and height must be numbers.", e);
        }

        if (width < GameConfig.BRICK_LENGTH || height < GameConfig.BRICK_LENGTH) {
            throw new IllegalArgumentException("Width and height should be > " + GameConfig.BRICK_LENGTH);
        }

        List<Brick> bricks = new ArrayList<>();
        for (int i = 2; i < inputParts.length; i++) {
            String brickString = inputParts[i];
            if (brickString.length() != GameConfig.BRICK_LENGTH + 1) {
                throw new IllegalArgumentException("Each block should have " + GameConfig.BRICK_LENGTH + " symbols.");
            }

            char alignmentPrefix = brickString.charAt(0);
            Alignment alignment = Alignment.fromPrefix(alignmentPrefix);
            if (alignment == null) {
                throw new IllegalArgumentException("Orientation should be either `H`(horizontal) or `V`(vertical).");
            }

            char[] symbols = new char[GameConfig.BRICK_LENGTH];
            for (int s = 0; s < GameConfig.BRICK_LENGTH; s++) {
                if (!GameConfig.ALLOWED_SYMBOLS.contains(brickString.charAt(s + 1))) {
                    throw new IllegalArgumentException("Only these symbols are allowed: `~`, `^`, `*`, `@`");
                }
                symbols[s] = brickString.charAt(s + 1);
            }

            bricks.add(Brick.builder()
                    .alignment(alignment)
                    .symbols(symbols)
                    .build());
        }

        return InitializationInput.builder()
                .boardWidth(width)
                .boardHeight(height)
                .bricks(bricks)
                .build();
    }

    public static List<Command> parseCommandInput(String input) {
        List<Command> commands = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            Command command = Command.fromPrefix(Character.toUpperCase(input.charAt(i)));
            if (command != null) {
                commands.add(command);
            }
            if (commands.size() == GameConfig.MAX_NUMBER_OF_COMMANDS) {
                break;
            }
        }

        return commands;
    }
}
