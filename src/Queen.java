import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(Color color) throws FileNotFoundException {
        super(PieceType.QUEEN, color);
    }

    @Override
    public ArrayList<CoorPair> findLegalMoves() {
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();
        ArrayList<CoorPair> legalMoves = new ArrayList<>();
        legalMoves.addAll(findLegalDiagonalMoves());
        legalMoves.addAll(findLegalHorizontalMoves());
        return legalMoves;
    }

    @Override
    public ArrayList<CoorPair> movesForCheck() {
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();
        ArrayList<CoorPair> potentialMoves = new ArrayList<>();
        potentialMoves.addAll(diagonalForCheck());
        potentialMoves.addAll(horizontalForCheck());
        return potentialMoves;
    }
}
