package client;

import actions.ParseInputAction;
import lombok.RequiredArgsConstructor;
import model.Command;
import model.InitializationInput;

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

    public void invalidInput() {
        System.out.println("> You have entered an invalid input.");
    }

    public List<Command> nextTurn(char[][] currentFrame, int frameNumber) {
        printFrame(currentFrame, frameNumber);

        System.out.println("> Enter up to 2 commands to process before moving to the next frame (valid commands are L,R,D):");
        String input = scanner.nextLine();
        return ParseInputAction.parseCommandInput(input);
    }

    public void gameOver(char[][] currentFrame, int frameNumber) {
        printFrame(currentFrame, frameNumber);
        System.out.println("> Game Over.");
    }

    public String startOver() {
        System.out.println("> Enter S to start over or Q to quit:");
        return scanner.nextLine();
    }

    public void quit() {
        System.out.println("> Thank you for playing Match-3!");
    }

    private static void printFrame(char[][] currentFrame, int frameNumber) {
        System.out.println("Frame " + frameNumber);

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
    }

}
