import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Bishop extends Piece {
    public Bishop(Color color) throws FileNotFoundException {
        super(PieceType.BISHOP, color);
    }

    @Override
    public ArrayList<CoorPair> findLegalMoves() {
        return findLegalDiagonalMoves();
    }

    @Override
    public ArrayList<CoorPair> movesForCheck() {
        return diagonalForCheck();
    }
}
