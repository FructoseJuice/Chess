package com.chess.common.Pieces;

import java.io.FileNotFoundException;

public class Bishop extends Piece {
    public Bishop(Color color) throws FileNotFoundException {
        super(PieceType.BISHOP, color);
    }

    @Override
    public long findPotentialMoves() {
        return findPotentialDiagonalMoves();
    }

    @Override
    public long movesForCheck() {
        return diagonalForCheck();
    }
}
