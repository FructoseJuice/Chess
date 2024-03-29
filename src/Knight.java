import Utils.BitBoard;
import Utils.CoorPair;

import java.io.FileNotFoundException;

public class Knight extends Piece {
    public Knight(Color color) throws FileNotFoundException {
        super(PieceType.KNIGHT, color);
    }

    @Override
    public Long findPotentialMoves() {
        long legalMovesBitBoard = 0L;
        CoorPair newMove = new CoorPair(-1, -1);

        int[][] movements = {
                {60, -120}, {120, -60}, {-60, -120}, {-120, -60},
                {60, 120}, {120, 60}, {-60, 120}, {-120, 60}
        };

        for (int[] movement : movements) {
            int dx = movement[0]; //X coordinate movement
            int dy = movement[1]; //Y coordinate movement

            newMove.setCoordinates(this.getXCoor() + dx, this.getYCoor() + dy);
            if (checkNewMove(newMove)) {
                legalMovesBitBoard = BitBoard.add(legalMovesBitBoard, newMove.getToken());
            }
        }

        return legalMovesBitBoard;
    }

    /**
     * Checks if a new move is valid
     *
     * @param newMove move player wants to make
     * @return if it's valid
     */
    private boolean checkNewMove(CoorPair newMove) {
        if(!newMove.isInBounds()) return false;

        if (Board.isSpaceOccupied(newMove)) {
            return Board.isOpponentPieceAtCoordinates(newMove.getToken(), color);
        } else {
            return true;
        }
    }

    /**
     * Check every move this knight can make, including those protecting their own pieces
     * @return bitboard of all possible moves
     */
    @Override
    public Long movesForCheck() {
        CoorPair newMove = new CoorPair(-1, -1);
        long potentialMovesBitBoard = 0L;

        int[][] movements = {
                {60, -120}, {120, -60}, {-60, -120}, {-120, -60},
                {60, 120}, {120, 60}, {-60, 120}, {-120, 60}
        };

        for (int[] movement : movements) {
            int dx = movement[0]; //X coordinate movement
            int dy = movement[1]; //Y coordinate movement

            newMove.setCoordinates(this.getXCoor() + dx, this.getYCoor() + dy);
            if (checkPotentialMoveForCheck(newMove)) {
                potentialMovesBitBoard = BitBoard.add(potentialMovesBitBoard, newMove.getToken());
            }
        }

        return potentialMovesBitBoard;
    }

    /**
     * Check all potential moves, including moves protecting one's own pieces
     * @param newMove potential move
     * @return if this move is potentially legal
     */
    private boolean checkPotentialMoveForCheck(CoorPair newMove) {
        if (!newMove.isInBounds()) return false;

        if (Board.isSpaceOccupied(newMove)) {
            return Board.isAllyPieceAtCoordinates(newMove.getToken(), color);
        } else {
            return true;
        }
    }
}
