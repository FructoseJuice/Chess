import java.io.FileNotFoundException;

public class Rook extends Piece {
    private boolean firstMove = true;

    public Rook(Color color) throws FileNotFoundException {
        super(PieceType.ROOK, color);
    }

    public void setFirstMoveFalse() {
        firstMove = false;
    }

    public boolean getFirstMoveStatus() {
        return firstMove;
    }

    @Override
    public Long findPotentialMoves() {
        if ( !this.getCoordinates().isInBounds() ) return 0L;

        return findPotentialHorizontalMoves();
    }

    @Override
    public Long movesForCheck() {
        if ( !this.getCoordinates().isInBounds() ) return 0L;

        return horizontalForCheck();
    }
}
