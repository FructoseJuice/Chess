import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(Color color) throws FileNotFoundException {
        super(PieceType.QUEEN, color);
    }

    @Override
    public ArrayList<CoorPair> findPotentialMoves() {
        //Make sure we're in bounds of board before calculations
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();

        ArrayList<CoorPair> legalMoves = new ArrayList<>();

        //Find all diagonal and horizontal moves
        legalMoves.addAll(findPotentialDiagonalMoves());
        legalMoves.addAll(findPotentialHorizontalMoves());

        return legalMoves;
    }

    @Override
    public ArrayList<CoorPair> movesForCheck() {
        //Make sure we're in bounds of board before calculations
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();

        ArrayList<CoorPair> potentialMoves = new ArrayList<>();

        //Find all diagonal and horizontal moves, including moves protecting pieces
        potentialMoves.addAll(diagonalForCheck());
        potentialMoves.addAll(horizontalForCheck());

        return potentialMoves;
    }
}
