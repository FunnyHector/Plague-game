package game;

import java.util.List;
import java.util.Scanner;

import game.items.Item;
import game.player.Player;
import game.player.Virus;
import game.world.Area;
import game.world.GroundSquare;

/**
 * This is a text-based UI client. It provides a tiny (hard-coded) world and a simple text
 * UI. It's playable. It will NOT be used in our final game. It is only here for testing.
 * 
 * @author Hector (Fang Zhao 300364061)
 *
 */
public class TextUI {

    private static final Scanner SCANNER = new Scanner(System.in);

    public TextUI() {

        Game game = setupGame();

        // game running!
        runGame(game);

        // when game stops, some clean-up process.
        gameStop(game);

        SCANNER.close();
    }

    private Game setupGame() {
        // mock up a world
        Game game = new Game(TestConst.world, TestConst.entrances);

        // mock player
        Player player = new Player(1, "Hector", Virus.T_Virus);
        game.joinPlayer(player);

        return game;
    }

    private void runGame(Game game) {

        Player player = game.getPlayerById(1);

        while (true) {
            // print board
            GroundSquare currentPosition = player.getPosition();
            Area currentArea = currentPosition.getArea();
            int width = currentArea.getWidth();
            int replaceIndex = currentPosition.y * (width + 1) + currentPosition.x;
            char[] chars = currentArea.toString().toCharArray();
            chars[replaceIndex] = player.getDirection().getChar();
            String boardString = new String(chars);
            System.out.println(boardString);

            // step run the game
            StepRun(game, player);
        }

    }

    private void StepRun(Game game, Player player) {
        char input = parseChar();

        switch (input) {
        case 'w':
            // Move forward
            if (!game.playerMoveForward(player)) {
                System.out.println("Failed to move forward");
            }
            break;
        case 's':
            // Move backward
            if (!game.playerMoveBackward(player)) {
                System.out.println("Failed to move backward");
            }
            break;
        case 'q':
            // Turn left
            game.playerTurnLeft(player);
            break;
        case 'e':
            // Turn right
            game.playerTurnRight(player);
            break;
        case 'a':
            // Move left
            if (!game.playerMoveLeft(player)) {
                System.out.println("Failed to move left");
            }
            break;
        case 'd':
            // move right
            if (!game.playerMoveRight(player)) {
                System.out.println("Failed to move right");
            }
            break;
        case 'f':
            // the player wants to unlock a chest
            if (!game.playerUnlockChest(player)) {
                System.out.println("Failed to unlock the chest");
            }
            break;
        case 'g':
            // the player wants to take items in a chest in front
            if (!game.playerTakeItemsInChest(player)) {
                System.out.println("No items was taken from chest");
            }
            break;
        case 'r':
            // the player wants to unlock a room
            if (!game.playerUnlockRoom(player)) {
                System.out.println("Failed to unlock the room");
            }
            break;
        case 't':
            // the player wants to enter a room
            if (!game.playerEnterExitRoom(player)) {
                System.out.println("Failed to enter/exit the room");
            }
            break;
        case 'i':
            // the player wants to see what's inside the inventory
            game.getPlayerInventory(player).stream().forEach(e -> System.out.println(e));
            break;
        case 'c':
            // the player wants to see time & health left
            System.out.println(
                    "Your health left: " + game.getPlayerHealth(player) + " seconds");
            System.out.println("World clock: " + game.getClock());
            break;
        case '1':
            // the player wants to use the first item in inventory
            List<Item> inventory = game.getPlayerInventory(player);
            if (inventory.size() > 0) {
                Item item = game.getPlayerInventory(player).get(0);
                game.playerUseItem(player, item);
            } else {
                System.out.println("Player has no time in inventory");
            }
            break;
        case '2':
            // the player wants to use the second item in inventory
            List<Item> inventory_2 = game.getPlayerInventory(player);
            if (inventory_2.size() > 1) {
                Item item = game.getPlayerInventory(player).get(1);
                game.playerUseItem(player, item);
            } else {
                System.out.println("Player has no time in inventory");
            }
            break;
        case '0':
            // the player wants to destroy the first item in inventory
            List<Item> inventory_3 = game.getPlayerInventory(player);
            if (inventory_3.size() > 0) {
                Item item = game.getPlayerInventory(player).get(0);
                game.playerDestroyItem(player, item);
            } else {
                System.out.println("Player has no time in inventory");
            }
            break;

        default:

        }

    }

    private void gameStop(Game game) {
        // TODO Need properly stop the game

    }

    private static char parseChar() {

        String line = SCANNER.nextLine();

        // if user asked for help, print out help message
        if (line.equals("help")) {
            helpMessage();
            line = SCANNER.nextLine();
        }

        while (line.length() > 1) {
            System.out.println("Please only type in one char");
            line = SCANNER.nextLine();
        }

        return line.charAt(0);
    }

    // @formatter:off
    /**
     * This method print out available input keys.
     */
    private static void helpMessage() {
        String s = "[Help]\n"
                + "Only lower case, and only one character.\n"
                + "Move forward: w\n"
                + "Move backward: s\n"
                + "Move left: a\n"
                + "Move right: d\n"
                + "Turn left: q\n"
                + "Trun right: e\n"
                + "Unlock chest: f\n"
                + "Take items in chest: g\n"
                + "Unlock door: r\n"
                + "Enter door: t\n"
                + "Open inventory: i\n"
                + "Clock & Time left: c\n"
                + "Use the 1st item in inventory: 1\n"
                + "Use the 2nd item in inventory: 2\n"
                + "Destroy the 1st item in inventory: 0\n";
        System.out.println(s);
    }
    // @formatter:on

    public static void main(String[] args) {
        new TextUI();
    }
}
