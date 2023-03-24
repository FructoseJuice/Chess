import java.io.FileNotFoundException;
import java.util.ArrayList;

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
    public ArrayList<CoorPair> findLegalMoves() {
        return findLegalHorizontalMoves();
    }

    @Override
    public ArrayList<CoorPair> movesForCheck() {
        return horizontalForCheck();
    }
}
