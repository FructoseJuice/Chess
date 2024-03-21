import java.io.FileNotFoundException;

public class Bishop extends Piece {
    public Bishop(Color color) throws FileNotFoundException {
        super(PieceType.BISHOP, color);
    }

    @Override
    public Long findPotentialMoves() {
        return findPotentialDiagonalMoves();
    }

    @Override
    public Long movesForCheck() {
        return diagonalForCheck();
    }
}
