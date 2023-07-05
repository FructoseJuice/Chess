import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(Color color) throws FileNotFoundException {
        super(PieceType.KNIGHT, color);
    }

    @Override
    public ArrayList<Integer> findPotentialMoves() {
        if (!this.getCoordinates().isInBounds()) {
            return new ArrayList<>();
        }

        ArrayList<Integer> legalMoves = new ArrayList<>();
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
                legalMoves.add(newMove.getToken());
            }
        }

        return legalMoves;
    }

    /**
     * Checks if a new move is valid
     *
     * @param newMove move player wants to make
     * @return if it's valid
     */
    private boolean checkNewMove(CoorPair newMove) {
        if (newMove.isInBounds()) {
            if (Main.currentPieceLocations[newMove.getToken()] != null) {
                return Main.currentPieceLocations[newMove.getToken()].color != this.color;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Check every move this knight can make, including those protecting their own pieces
     * @return List of all possible moves
     */
    @Override
    public ArrayList<Integer> movesForCheck() {
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();

        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<Integer> potentialMoves = new ArrayList<>();

        int[][] movements = {
                {60, -120}, {120, -60}, {-60, -120}, {-120, -60},
                {60, 120}, {120, 60}, {-60, 120}, {-120, 60}
        };

        for (int[] movement : movements) {
            int dx = movement[0]; //X coordinate movement
            int dy = movement[1]; //Y coordinate movement

            newMove.setCoordinates(this.getXCoor() + dx, this.getYCoor() + dy);
            if (checkPotentialMoveForCheck(newMove)) {
                potentialMoves.add(newMove.getToken());
            }
        }

        return potentialMoves;
    }

    /**
     * Check all potential moves, including moves protecting one's own pieces
     * @param newMove potential move
     * @return if this move is potentially legal
     */
    private boolean checkPotentialMoveForCheck(CoorPair newMove) {
        if (newMove.isInBounds()) {
            if (Main.currentPieceLocations[newMove.getToken()] != null) {
                return Main.currentPieceLocations[newMove.getToken()].color == this.color;
            } else {
                return true;
            }
        }
        return false;
    }
}
