public class Board {

  private Square[][] board = new Square[8][8];

  public Board(char whiteGap, char blackGap) {
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        board[i][j] = new Square(j, i, Colour.NONE);
      }
    }
    if (Character.toLowerCase(whiteGap) < 'a'
        || Character.toLowerCase(whiteGap) > 'h'
        || Character.toLowerCase(blackGap) < 'a'
        || Character.toLowerCase(blackGap) > 'h') {
      throw new IllegalArgumentException("Invalid initialisation");
    }
    setBlackGap(Character.toLowerCase(blackGap));
    setWhiteGap(Character.toLowerCase(whiteGap));
  }

  private void setWhiteGap(char whiteGap) {
    for (int i = 0; i < 8; i++) {
      if (whiteGap - 'a' == i) {
        continue;
      }
      board[1][i].setOccupier(Colour.WHITE);
    }
  }

  public void setBoard(Square[][] board) {
    this.board = board;
  }

  private void setBlackGap(char blackGap) {
    for (int i = 0; i < 8; i++) {
      if (blackGap - 'a' == i) {
        continue;
      }
      board[6][i].setOccupier(Colour.BLACK);
    }
  }

  public Square getSquare(int x, int y) {
    return board[y][x];
  }

  public Square getSquare(char x, char y) {
    return getSquare(x - 'a', y - '1');
  }

  public void applyMove(Move move) {
    if (!move.isEnPassantCapture()) {
      move.getTo().setOccupier(move.getFrom().occupiedBy());
      move.getFrom().setOccupier(Colour.NONE);
      return;
    }

    getSquare(move.getTo().getX(), move.getFrom().getY()).setOccupier(Colour.NONE);
    move.getTo().setOccupier(move.getFrom().occupiedBy());
    move.getFrom().setOccupier(Colour.NONE);
  }

  public void unapplyMove(Move move) {
    move.getFrom().setOccupier(move.getTo().occupiedBy());
    if (move.isCapture() && !move.isEnPassantCapture()) {
      if (move.getFrom().occupiedBy() == Colour.WHITE) {
        move.getTo().setOccupier(Colour.BLACK);
      }
      move.getTo().setOccupier(Colour.WHITE);
    }

    if (move.isEnPassantCapture()) {
      move.getFrom().setOccupier(move.getTo().occupiedBy());
      if (move.getFrom().occupiedBy() == Colour.WHITE) {
        getSquare(move.getTo().getX(), move.getFrom().getY()).setOccupier(Colour.BLACK);
        move.getTo().setOccupier(Colour.NONE);
        return;
      }
      getSquare(move.getTo().getX(), move.getFrom().getY()).setOccupier(Colour.WHITE);
    }
    move.getTo().setOccupier(Colour.NONE);
  }

  public boolean gameOver() {
    for (int i = 0; i < 8; i++) {
      if (board[0][i].occupiedBy() != Colour.NONE || board[7][i].occupiedBy() != Colour.NONE) {
        return true;
      }
    }
    return false;
  }

  public String toString() {
    StringBuffer display = new StringBuffer("   A B C D E F G H\n");
    for (int i = 7; i >= 0; i--) {
      display.append(i + 1);
      display.append("  ");
      for (int j = 0; j < 8; j++) {
        display.append(board[i][j].display());
        display.append(" ");
      }
      display.append("\t" + (i + 1) + "\n");
    }
    display.append("   A B C D E F G H\n");

    return display.toString();
  }

  public Board clone() {
    Board boardNew = new Board('A', 'A');
    Square[][] board1 = new Square[8][8];
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        board1[i][j] = board[i][j].clone();
      }
    }
    boardNew.setBoard(board1);
    return boardNew;
  }
}
