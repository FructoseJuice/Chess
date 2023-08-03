import java.io.FileNotFoundException;

public class Queen extends Piece {
    public Queen(Color color) throws FileNotFoundException {
        super(PieceType.QUEEN, color);
    }

    @Override
    public Long findPotentialMoves() {
        //Make sure we're in bounds of board before calculations
        if ( !this.getCoordinates().isInBounds() ) return 0L;

        Long legalMovesBitBoard = 0L;

        //Find all diagonal and horizontal moves
        legalMovesBitBoard |= findPotentialDiagonalMoves();
        legalMovesBitBoard |= findPotentialHorizontalMoves();

        return legalMovesBitBoard;
    }

    @Override
    public Long movesForCheck() {
        //Make sure we're in bounds of board before calculations
        if ( !this.getCoordinates().isInBounds() ) return 0L;

        Long potentialMoves = 0L;

        //Find all diagonal and horizontal moves, including moves protecting pieces
        potentialMoves |= diagonalForCheck();
        potentialMoves |= horizontalForCheck();

        return potentialMoves;
    }
}
