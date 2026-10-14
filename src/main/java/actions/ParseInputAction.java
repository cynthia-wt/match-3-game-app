package actions;

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
        int width = Integer.parseInt(inputParts[0]);
        int height = Integer.parseInt(inputParts[1]);

        List<Brick> bricks = new ArrayList<>();
        for (int i = 2; i < inputParts.length; i++) {
            String brickString = inputParts[i];
            char alignmentPrefix = brickString.charAt(0);
            Alignment alignment = Alignment.fromPrefix(alignmentPrefix);

            char[] symbols = new char[3];
            symbols[0] = brickString.charAt(1);
            symbols[1] = brickString.charAt(2);
            symbols[2] = brickString.charAt(3);

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
            Command command = Command.fromPrefix(input.charAt(i));
            if (command != null) {
                commands.add(command);
            }
            if (commands.size() == 2) {
                break;
            }
        }

        return commands;
    }
}
