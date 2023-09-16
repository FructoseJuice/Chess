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
        legalMovesBitBoard = BitBoard.add(legalMovesBitBoard, findPotentialDiagonalMoves());
        legalMovesBitBoard = BitBoard.add(legalMovesBitBoard, findPotentialHorizontalMoves());

        return legalMovesBitBoard;
    }

    @Override
    public Long movesForCheck() {
        //Make sure we're in bounds of board before calculations
        if ( !this.getCoordinates().isInBounds() ) return 0L;

        long potentialMovesBitBoard = 0L;

        //Find all diagonal and horizontal moves, including moves protecting pieces
        potentialMovesBitBoard = BitBoard.add(potentialMovesBitBoard, diagonalForCheck());
        potentialMovesBitBoard = BitBoard.add(potentialMovesBitBoard, horizontalForCheck());

        return potentialMovesBitBoard;
    }
}
