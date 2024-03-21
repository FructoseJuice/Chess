import Utils.BitBoard;

import java.io.FileNotFoundException;

public class Queen extends Piece {
    public Queen(Color color) throws FileNotFoundException {
        super(PieceType.QUEEN, color);
    }

    @Override
    public Long findPotentialMoves() {
        //Make sure we're in bounds of board before calculations
        if ( !this.getCoordinates().isInBounds() ) return 0L;

        long legalMovesBitBoard = 0L;

        //Find all diagonal and horizontal moves
        legalMovesBitBoard = BitBoard.merge(legalMovesBitBoard, findPotentialDiagonalMoves());
        legalMovesBitBoard = BitBoard.merge(legalMovesBitBoard, findPotentialHorizontalMoves());

        return legalMovesBitBoard;
    }

    @Override
    public Long movesForCheck() {
        //Make sure we're in bounds of board before calculations
        if ( !this.getCoordinates().isInBounds() ) return 0L;

        long potentialMovesBitBoard = 0L;

        //Find all diagonal and horizontal moves, including moves protecting pieces
        potentialMovesBitBoard = BitBoard.merge(potentialMovesBitBoard, diagonalForCheck());
        potentialMovesBitBoard = BitBoard.merge(potentialMovesBitBoard, horizontalForCheck());

        return potentialMovesBitBoard;
    }
}
