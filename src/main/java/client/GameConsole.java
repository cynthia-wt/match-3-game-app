package client;

import lombok.RequiredArgsConstructor;
import model.Command;
import model.InitializationInput;
import actions.ParseInputAction;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class GameConsole {

    private final Scanner scanner = new Scanner(System.in);

    public InitializationInput initialiseGame() {
        System.out.println("> Please enter field size (width and height) and up to 5 bricks set:");
        System.out.println(">");
        System.out.println("> Example: `5 8 H^^* V*@^`");

        String input = scanner.nextLine();
        return ParseInputAction.parseInitializationInput(input);
    }

    public List<Command> nextTurn(char[][] currentFrame, int turnNumber) {
        System.out.println("Frame " + turnNumber);

        for (char[] row : currentFrame) {
            StringBuilder line = new StringBuilder("| ");
            for (int c = 0; c < row.length; c++) {
                line.append(row[c]);
                if (c < row.length - 1) {
                    line.append(' ');       // space between cells, none after the last
                }
            }
            line.append(" |");
            System.out.println(line);
        }

        System.out.println("> Enter up to 2 commands to process before moving to the next frame (valid commands are L,R,D):");
        String input = scanner.nextLine();
        return ParseInputAction.parseCommandInput(input);
    }

}
