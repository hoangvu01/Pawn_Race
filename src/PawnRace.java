import java.util.Scanner;

public class PawnRace {

  public static void main(String[] args) {
    Board board;

    Game game;
    Player one;
    Player two;

    Scanner scanner = new Scanner(System.in);
    String input;

    while (true) {
      try {
        System.out.println("Welcome to PawnRace");
        System.out.println("Please initialise the board");
        System.out.println("<white> <black> <white gap> <black gap>");
        input = scanner.nextLine();
        board = new Board(input.charAt(4), input.charAt(6));
        game = new Game(board);
        one = new Player(game, board, Colour.WHITE, input.charAt(0));
        two = new Player(game, board, Colour.BLACK, input.charAt(2));
        break;
      } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
        System.out.println(e.getMessage());
        System.out.println("Please enter a valid column");
        System.out.println("----------------------------");
        continue;
      }
    }

    while (true) {
      try {
        System.out.println(board);
        if (game.isFinished()) {
          System.out.println("Congratulations!");
          System.out.println(game.getGameResult() + " wins");
          System.exit(1);
        }
        System.out.println("It's " + game.getCurrentPlayer() + "'s turn to move!");
        if (game.getCurrentPlayer() == one.getColour()) {
          if (one.getAllValidMoves().length == 0) {
            System.out.println("GAME DRAWN!");
            System.exit(1);
          }
          if (one.isComputerPlayer()) {
            game.applyMove(one.makeMoveRand());
            continue;
          }
        }
        if (game.getCurrentPlayer() == two.getColour()) {
          if (two.getAllValidMoves().length == 0) {
            System.out.println("GAME DRAWN!");
            System.exit(1);
          }
          if (two.isComputerPlayer()) {
            game.applyMove(two.makeMove());
            continue;
          }
        }

        input = scanner.nextLine();
        if (input.equals("quit")) {
          System.exit(1);
        }
        if (input.equals("undo")) {
          game.unapplyMove();
          game.unapplyMove();
        }
        game.applyMove(input);
      } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
        System.out.println("Invalid move please try again");
      }
    }

//            Board board = new Board('h', 'h');
//            Game game = new Game(board);
//            Player one = new Player(game, board, Colour.WHITE, 'C');
//            Player two = new Player(game, board, Colour.BLACK, 'C');

//            game.applyMove("a4");
//            game.applyMove("a5");
//            game.applyMove("b4");
//            game.applyMove("b5");
//            game.applyMove("c4");
//            game.applyMove("c5");
//            game.applyMove("d4");
//            game.applyMove("d5");
//            game.applyMove("e4");
//            game.applyMove("e5");
//            game.applyMove("f4");
//            game.applyMove("f5");
//            game.applyMove("g4");
//            game.applyMove("g5");
//            game.applyMove("a4xb5");
//            System.out.println(one.getPassedPawns().get(0).toString());
//            game.applyMove("a4");
//            System.out.println(board);
  }
}
