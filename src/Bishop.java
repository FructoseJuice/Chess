import java.io.FileNotFoundException;

public class Bishop extends Piece {
    public Bishop(Color color) throws FileNotFoundException {
        super(PieceType.BISHOP, color);
    }

    @Override
    public Long findPotentialMoves() {
        if ( !this.getCoordinates().isInBounds() ) return 0L;

        return findPotentialDiagonalMoves();
    }

    @Override
    public Long movesForCheck() {
        if ( !this.getCoordinates().isInBounds() ) return 0L;

        return diagonalForCheck();
    }
}
