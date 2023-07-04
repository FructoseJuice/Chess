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
    public ArrayList<Integer> findPotentialMoves() {
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();

        return findPotentialHorizontalMoves();
    }

    @Override
    public ArrayList<Integer> movesForCheck() {
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();

        return horizontalForCheck();
    }
}
