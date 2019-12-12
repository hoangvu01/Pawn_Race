import static java.lang.Math.sqrt;

import java.util.*;

public class Player {

    private Colour colour;
    private boolean isBot;

    public Board getBoard() {
        return board;
    }

    private Board board;
    private Game game;

    public Player(Game game, Board board, Colour colour, char player) {
        if (Character.toLowerCase(player) != 'c' && Character.toLowerCase(player) != 'p') {
            throw new IllegalArgumentException(
                    "Enter a valid player \'C\' for Computer or \'P\' for Human");
        }
        this.colour = colour;
        this.board = board;
        this.game = game;
        isBot = Character.toLowerCase(player) == 'c';
    }

    public Player(Game game, Board board, Colour colour, boolean isComputerPlayer) {
        this(game, board, colour, isComputerPlayer ? 'c' : 'p');
    }

    public Colour getColour() {
        return colour;
    }

    public boolean isComputerPlayer() {
        return isBot;
    }

    public Move makeMoveRand() {
        Random rand = new Random();
        int moveIndex = rand.nextInt(getAllValidMoves().length);
        return getAllValidMoves()[moveIndex];
    }

    public Move pushPassedPawn() {
        if (getPassedPawns() == null || getPassedPawns().size() == 0) {
            return null;
        }

        Square bestPawn = getPassedPawns().get(0);

        for (Square pawn : getPassedPawns()) {
            if (colour == Colour.WHITE && pawn.getY() > bestPawn.getY()) {
                bestPawn = pawn;
            }

            if (colour == Colour.BLACK && pawn.getY() < bestPawn.getY()) {
                bestPawn = pawn;
            }
        }

        return new Move(bestPawn, pushPawn(bestPawn), false, false);
    }

    private Square pushPawn(Square from) {
        if (colour == Colour.BLACK) {
            return board.getSquare(from.getX(), from.getY() - 1);
        }
        return board.getSquare(from.getX(), from.getY() + 1);

    }

    public Move makeMove() {
        if (pushPassedPawn() != null || getPassedPawns().size() != 0) {
            return pushPassedPawn();
        }

        Move toMove = getAllValidMoves()[0];
        int eval;
        System.out.println("Thinking...");
        if (colour == Colour.BLACK) {
            int maxEval = -1000;
            for (Move m : getAllValidMoves()) {
                eval = minimax(child(m), 7, -1000, 1000, true);

                if (eval >= maxEval) {
                    toMove = m;
                    maxEval = eval;
                }
            }
        } else {

            int minEval = 1000;
            for (Move m : getAllValidMoves()) {
                eval = minimax(child(m), 7, -1000, 1000, false);
                minEval = Integer.min(eval, minEval);

                if (eval <= minEval) {
                    toMove = m;
                    minEval = eval;
                }
            }
        }
        System.out.println(toMove.getSAN());
        return toMove;
    }

    public Square[] getAllPawns() {
        return getPawns(colour);
    }

    private Square[] getPawns(Colour colour) {
        List<Square> pawns = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getSquare(i, j).occupiedBy() == colour) {
                    pawns.add(board.getSquare(i, j));
                }
            }
        }
        Square[] pawnsList = new Square[pawns.size()];
        Collections.shuffle(pawns);
        pawnsList = pawns.toArray(pawnsList);
        return pawnsList;
    }

    private int evalWhite() {
        int maxDistance = 0;
        int sndDistance = 0;
        int captureCount = 0;
        int passPawns = 0;

        for (Square pawn : getPassedPawns()) {
            int distance = pawn.getY() - 2;
            maxDistance = Integer.max(maxDistance, pawn.getY() - 2);
            if (distance > maxDistance) {
                sndDistance = maxDistance;
                maxDistance = distance;
            } else if (distance > sndDistance && distance != maxDistance) {
                sndDistance = distance;
            }
        }

        for (Move move : getValidMovesFrom(Colour.WHITE)) {
            if (move.getIsCapture()) {
                captureCount++;
            }

            if (isColumnEmpty(move.getTo())) {
                passPawns++;
            }
        }
        return getPawns(Colour.WHITE).length * 65 + 3 * maxDistance ^ 2 + sndDistance ^ 2 + captureCount * 10 + passPawns * 20;
    }

    private int evalBlack() {
        int maxDistance = 0;
        int sndDistance = 0;
        int captureCount = 0;
        int passPawns = 0;

        for (Square pawn : getPassedPawns()) {
            int distance = 7 - pawn.getY();
            maxDistance = Integer.max(maxDistance, pawn.getY() - 2);
            if (distance > maxDistance) {
                sndDistance = maxDistance;
                maxDistance = distance;
            } else if (distance > sndDistance && distance != maxDistance) {
                sndDistance = distance;
            }
        }

        for (Move move : getValidMovesFrom(Colour.BLACK)) {
            if (move.getIsCapture()) {
                captureCount++;
            }

            if (isColumnEmpty(move.getTo())) {
                passPawns++;
            }
        }


        return getPawns(Colour.BLACK).length * 65 + 3 * maxDistance ^ 2 + sndDistance ^ 2 + captureCount * 10 + passPawns * 20;
    }

    private int evalBoard() {
        return evalBlack() - evalWhite();
    }

    public Game getGame() {
        return game;
    }

    private int minimax(Player p, int depth, int alpha, int beta, boolean maximisngPlayer) {
        int eval;
        if (depth == 0 || p.getGame().isFinished()) {
//      System.out.println(p.getBoard());
            return p.evalBoard();
        }
        if (maximisngPlayer) {
            int maxEval = -1000;
            for (Move m : p.getAllValidMoves()) {
                eval = p.minimax(p.child(m), depth - 1, alpha, beta, false);
                maxEval = Integer.max(eval, maxEval);

                // for speed op, delete if bug
                alpha = Integer.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = 1000;
            for (Move m : p.getAllValidMoves()) {
                eval = p.minimax(p.child(m), depth - 1, alpha, beta, true);
                minEval = Integer.min(eval, minEval);

                // for speed op, delete if bug
                beta = Integer.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    private Player child(Move m) {
        Board board1 = board.clone();
        Game game1 = game.clone(board1);
        Square from = board1.getSquare(m.getFrom().getX(), m.getFrom().getY());
        Square to = board1.getSquare(m.getTo().getX(), m.getTo().getY());
        Move m1 = new Move(from, to, m.getIsCapture(), m.isEnPassantCapture());
        game1.applyMove(m1);
        return new Player(game1, game1.getBoard(),
                (colour == Colour.WHITE ? Colour.BLACK : Colour.WHITE), 'c');
    }

    public List<Square> getPassedPawns() {
        List<Square> passedPawns = new ArrayList<>();
        for (Square pawn : getAllPawns()) {
            if (!isColumnEmpty(pawn.getX(), pawn.getY())) {
                continue;
            }

            if (pawn.getX() > 0) {
                if (!isColumnEmpty(pawn.getX() - 1, pawn.getY())) {
                    continue;
                }
            }

            if (pawn.getX() < 7) {
                if (!isColumnEmpty(pawn.getX() + 1, pawn.getY())) {
                    continue;
                }
            }

            passedPawns.add(pawn);
        }
        return passedPawns;
    }

    private boolean isColumnEmpty(int x, int y, Colour colour) {
        if (colour == Colour.WHITE) {
            for (int i = 7; i > y; i--) {
                if (board.getSquare(x, i).occupiedBy() != Colour.NONE) {
                    return false;
                }
            }
        }

        if (colour == Colour.BLACK) {
            for (int i = 0; i < y; i++) {
                if (board.getSquare(x, i).occupiedBy() != Colour.NONE) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isColumnEmpty(int x, int y) {
        return isColumnEmpty(x, y, this.colour);
    }

    private boolean isColumnEmpty(Square pawn) {
        return isColumnEmpty(pawn.getX(), pawn.getY(), pawn.occupiedBy());
    }

//  private boolean isColumnEmpty(int x, int y) {
//    if (colour == Colour.WHITE) {
//      for (int i = 7; i > y; i--) {
//        if (board.getSquare(x, i).occupiedBy() != Colour.NONE) {
//          return false;
//        }
//      }
//    }
//
//    if (colour == Colour.BLACK) {
//      for (int i = 0; i < y; i++) {
//        if (board.getSquare(x, i).occupiedBy() != Colour.NONE) {
//          return false;
//        }
//      }
//    }
//    return true;
//  }

    public Move[] getValidMovesFrom(Colour colour) {
        List<Move> allMoves = new ArrayList<>();
        for (Square pawn : getPawns(colour)) {
            allMoves.addAll(tryAllMoves(pawn));
        }
        Move[] finalMoves = new Move[allMoves.size()];
        finalMoves = allMoves.toArray(finalMoves);
        return finalMoves;
    }

    public Move[] getAllValidMoves() {
        List<Move> allMoves = new ArrayList<>();
        for (Square pawn : getAllPawns()) {
            allMoves.addAll(tryAllMoves(pawn));
        }
        Move[] finalMoves = new Move[allMoves.size()];
        finalMoves = allMoves.toArray(finalMoves);
        return finalMoves;
    }

    private List<Move> tryAllMoves(Square pawn) {
        List<Move> allMoves = new ArrayList<>();

        if (colour == Colour.WHITE) {
            if (pawn.getX() != 0
                    && tryMove(pawn, board.getSquare(pawn.getX() - 1, pawn.getY() + 1)) != null) {
                allMoves.add(tryMove(pawn, board.getSquare(pawn.getX() - 1, pawn.getY() + 1)));
            }
            if (pawn.getX() != 7
                    && tryMove(pawn, board.getSquare(pawn.getX() + 1, pawn.getY() + 1)) != null) {
                allMoves.add(tryMove(pawn, board.getSquare(pawn.getX() + 1, pawn.getY() + 1)));
            }
            if (pawn.getY() == 1 && tryMove(pawn, board.getSquare(pawn.getX(), 3)) != null) {
                allMoves.add(tryMove(pawn, board.getSquare(pawn.getX(), 3)));
            }
            if (tryMove(pawn, board.getSquare(pawn.getX(), pawn.getY() + 1)) != null) {
                allMoves.add(tryMove(pawn, board.getSquare(pawn.getX(), pawn.getY() + 1)));
            }
        }

        if (colour == Colour.BLACK) {
            if (pawn.getX() != 0
                    && tryMove(pawn, board.getSquare(pawn.getX() - 1, pawn.getY() - 1)) != null) {
                allMoves.add(tryMove(pawn, board.getSquare(pawn.getX() - 1, pawn.getY() - 1)));
            }
            if (pawn.getX() != 7
                    && tryMove(pawn, board.getSquare(pawn.getX() + 1, pawn.getY() - 1)) != null) {
                allMoves.add(tryMove(pawn, board.getSquare(pawn.getX() + 1, pawn.getY() - 1)));
            }
            if (pawn.getY() == 6 && tryMove(pawn, board.getSquare(pawn.getX(), 4)) != null) {
                allMoves.add(tryMove(pawn, board.getSquare(pawn.getX(), 4)));
            }
            if (tryMove(pawn, board.getSquare(pawn.getX(), pawn.getY() - 1)) != null) {
                allMoves.add(tryMove(pawn, board.getSquare(pawn.getX(), pawn.getY() - 1)));
            }
        }

        return allMoves;
    }

    private Move tryMove(Square from, Square to) {

        StringBuilder move = new StringBuilder("");
        move.append(from.getXasChar())
                .append(from.getY() + 1)
                .append("-" + to.getXasChar())
                .append(to.getY() + 1);

        if (game.parseMove(move.toString()) != null) {
            return (new Move(from, to, false, false));
        }

        move.setCharAt(2, 'x');
        if (game.isEnPassantValid(from, to)) {
            return (new Move(from, to, true, true));
        }
        if (game.parseMove(move.toString()) != null) {
            return (new Move(from, to, true, false));
        }
        return null;
    }
}
