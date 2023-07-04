import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Bishop extends Piece {
    public Bishop(Color color) throws FileNotFoundException {
        super(PieceType.BISHOP, color);
    }

    @Override
    public ArrayList<Integer> findPotentialMoves() {
        return findPotentialDiagonalMoves();
    }

    @Override
    public ArrayList<Integer> movesForCheck() {
        return diagonalForCheck();
    }
}
