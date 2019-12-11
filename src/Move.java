public class Move{

    private Square from;
    private Square to;
    private boolean capture;
    private boolean enPassantCapture;

    public Move(Square from, Square to, boolean isCapture, boolean isEnPassantCapture) {
        this.from = from;
        this.to = to;
        this.capture = isCapture;
        this.enPassantCapture = isEnPassantCapture;
    }

    public Square getFrom(){
        return from;
    }

    public Square getTo(){
        return to;
    }

    public boolean getIsCapture() {
        return capture;
    }

    public boolean isCapture(){
        if (from.occupiedBy() == to.occupiedBy()
                || from.occupiedBy() != Colour.NONE
                || to.occupiedBy() != Colour.NONE) {
            return false;
        }
        if (from.occupiedBy() == Colour.WHITE) {
            if (from.getY() + 1 != to.getY())
                return false;
            if (from.getX() != to.getX() + 1
                    && from.getX() != to.getX() - 1)
                return false;
        }
        if (from.occupiedBy() == Colour.BLACK) {
            if (from.getY() - 1 != to.getY())
                return false;
            if (from.getX() != to.getX() + 1
                    && from.getX() != to.getX() - 1)
                return false;
        }
        return true;
    }

    public boolean isEnPassantCapture(){
        return enPassantCapture;
    }

    public String getSAN(){
        StringBuffer move = new StringBuffer("");
        if (capture) {
            move.append(from.getXasChar() );
            move.append(from.getY() + 1);
            move.append("x");
        }
        move.append( to.getXasChar());
        move.append(to.getY() + 1);
        return move.toString();
    }

    @Override
    public String toString() {
        return from.toString() + " - " + to.toString();
    }

    public Move clone(){
        return new Move(from.clone(), to.clone(), isCapture(),isEnPassantCapture());
    }
}