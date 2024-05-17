package main;

import java.io.FileNotFoundException;

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
    public long findPotentialMoves() {
        return findPotentialHorizontalMoves();
    }

    @Override
    public long movesForCheck() {
        return horizontalForCheck();
    }
}
