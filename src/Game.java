import java.util.ArrayList;
import java.util.List;

public class Game {


  private boolean whitePlayer;
  private Board board;
  private List<Move> moves = new ArrayList<>();
  private int moveIndex;

  public Game(Board board) {
    this.board = board;
    whitePlayer = true;
    moveIndex = 0;
  }

  public void setWhitePlayer(boolean whitePlayer) {
    this.whitePlayer = whitePlayer;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public void setMoves(List<Move> moves) {
    this.moves = moves;
  }

  public void setMoveIndex(int moveIndex) {
    this.moveIndex = moveIndex;
  }


  public Colour getCurrentPlayer() {
    return (whitePlayer ? Colour.WHITE : Colour.BLACK);
  }

  public Move getLastMove() {
    return (moveIndex > 0 ? moves.get(moveIndex - 1) : null);
  }

  public void applyMove(Move move) {
    board.applyMove(move);
    moves.add(move);
    whitePlayer = !whitePlayer;
    moveIndex++;
  }

  public void applyMove(String san) {
    Move move = parseMove(san);
    if (move == null) {
      return;
    }
    applyMove(move);
  }

  public void unapplyMove() {
    if (moveIndex < 1) {
      return;
    }
    board.unapplyMove(moves.get(moveIndex - 1));
    moveIndex--;
    moves.remove(moves.size() - 1);
    whitePlayer = !whitePlayer;
  }

  public boolean isFinished() {
    return board.gameOver();
  }

  public Colour getGameResult() {
    if (isFinished()) {
      return (whitePlayer ? Colour.BLACK : Colour.WHITE);
    }
    return Colour.NONE;
  }

  public Move parseMove(String san) {
    Square from;
    Square to;

    if (san.length() != 2 && san.length() != 5) {
      return null;
    }

    if (san.length() == 2) {
      from = findFromMove(san.charAt(0) - 'a', san.charAt(1) - '1');
      to = board.getSquare(san.charAt(0), san.charAt(1));
      if (from == null) {
        return null;
      }
      return (new Move(from, to, false, false));
    }

    if (san.length() == 5) {
      from = board.getSquare(san.charAt(0), san.charAt(1));
      to = board.getSquare(san.charAt(3), san.charAt(4));

      if (san.charAt(2) == '-' && isMoveValid(from, to)) {
        return (new Move(from, to, false, false));
      }

      if (san.charAt(2) == 'x' && isCaptureValid(from, to)) {
        return (new Move(from, to, true, false));
      }

      if (san.charAt(2) == 'x' && isEnPassantValid(from, to)) {
        return (new Move(from, to, true, true));
      }
    }
    return null;
  }

  public Square findFromMove(Square to) {
    return findFromMove(to.getX(), to.getY());
  }

  public Square findFromMove(int x, int y) {
    if (board.getSquare(x, y).occupiedBy() != Colour.NONE) {
      return null;
    }

    if (y == 3
        && whitePlayer
        && board.getSquare(x, 1).occupiedBy() == Colour.WHITE
        && board.getSquare(x, 2).occupiedBy() == Colour.NONE) {
      return board.getSquare(x, 1);
    }

    if (y == 4
        && !whitePlayer
        && board.getSquare(x, 6).occupiedBy() == Colour.BLACK
        && board.getSquare(x, 5).occupiedBy() == Colour.NONE) {
      return board.getSquare(x, 6);
    }

    if (whitePlayer && board.getSquare(x, y - 1).occupiedBy() == Colour.WHITE) {
      return board.getSquare(x, y - 1);
    }

    if (!whitePlayer && board.getSquare(x, y + 1).occupiedBy() == Colour.BLACK) {
      return board.getSquare(x, y + 1);
    }
    return null;
  }

  public boolean isMoveValid(Square from, Square to) {
    if (to.occupiedBy() != Colour.NONE) {
      return false;
    }

    if (to.getX() != from.getX()) {
      return false;
    }

    if (findFromMove(to) == null) {
      return false;
    }

    return true;
  }

  public boolean isCaptureValid(Square from, Square to) {
    if (from.occupiedBy() == to.occupiedBy() || from.occupiedBy() == Colour.NONE) {
      return false;
    }
    if (to.occupiedBy() == Colour.NONE) {
      return false;
    }
    if (from.occupiedBy() == Colour.WHITE) {
      if (from.getX() != to.getX() + 1 && from.getX() != to.getX() - 1) {
        return false;
      }
      if (from.getY() + 1 != to.getY()) {
        return false;
      }
    }

    if (from.occupiedBy() == Colour.BLACK) {
      if (from.getX() != to.getX() + 1 && from.getX() != to.getX() - 1) {
        return false;
      }
      if (from.getY() - 1 != to.getY()) {
        return false;
      }
    }
    return true;
  }

  public boolean isEnPassantValid(Square from, Square to) {
    if (moveIndex < 3) {
      return false;
    }
    if (to.occupiedBy() != Colour.NONE) {
      return false;
    }

    if (from.occupiedBy() != getCurrentPlayer()) {
      return false;
    }

    Move lastMove = getLastMove();

    if (whitePlayer && lastMove.getTo().getY() != 4) {
      return false;
    }

    if (!whitePlayer && lastMove.getTo().getY() != 3) {
      return false;
    }

    if (lastMove.getTo().getX() - 1 != from.getX()
        && lastMove.getTo().getX() + 1 != from.getX()) {
      return false;
    }

    if (whitePlayer && to.getY() - 1 != lastMove.getTo().getY()) {
      return false;
    }

    if (whitePlayer && from.getY() != 4) {
      return false;
    }

    if (!whitePlayer && to.getY() + 1 != lastMove.getTo().getY()) {
      return false;
    }

    if (!whitePlayer && from.getY() != 3) {
      return false;
    }

    return true;
  }


  public Game clone(Board board) {
    Game game1 = new Game(board);
    game1.setBoard(board);
    game1.setMoveIndex(moveIndex);
    List<Move> moves1 = new ArrayList<>();
    for (Move m : moves) {
      moves1.add(m.clone());
    }
    game1.setMoves(moves1);
    game1.setWhitePlayer(whitePlayer);
    return game1;
  }

}
