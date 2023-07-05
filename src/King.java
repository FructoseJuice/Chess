import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

public class King extends Piece {
    private boolean firstMove = true;

    public King(Color color) throws FileNotFoundException {
        super(PieceType.KING, color);
    }

    public void setFirstMoveFalse() {
        firstMove = false;
    }

    @Override
    public ArrayList<Integer> findPotentialMoves() {
        //Gets the potential legal moves this king can make
        ArrayList<Integer> legalMoves = getPotentialMoves();

        //Gets all possible moves of the opponent
        HashSet<Integer> opponentMoves = spacesOpponentCanMove();

        //If one of our potential moves is the same as an opponents possible move, then it's not legal as it
        //Puts the king in check
        legalMoves.removeIf(opponentMoves::contains);

        //Checks if a king can castle
        //Can only castle if it's our first move, and we're not in check
        if (firstMove && !Main.isKingInCheck(this.color)) {
            boolean rightRookCanCastle = false;
            boolean leftRookCanCastle = false;
            for (Piece piece : (this.color == Color.WHITE) ? Main.whitePieces : Main.blackPieces) {
                if (piece.pieceType == PieceType.ROOK) {
                    //Checks if the rook can still castle
                    if (((Rook) piece).getFirstMoveStatus()) {
                        if (piece.getCoordinates().coorEquals(new CoorPair(this.getXCoor() + 180, this.getYCoor()))) {
                            rightRookCanCastle = true;
                        }
                        if (piece.getCoordinates().coorEquals(new CoorPair(this.getXCoor() - 240, this.getYCoor()))) {
                            leftRookCanCastle = true;
                        }
                    }
                }
            }

            /*
            Only add castle as legal if the rook hasn't moved, and
            we're not castling through, or into check.
             */
            if (rightRookCanCastle &
                    !opponentMoves.contains(new CoorPair(this.getXCoor() + 60, this.getYCoor()).getToken()) &
                    !opponentMoves.contains(new CoorPair(this.getXCoor() + 120, this.getYCoor()).getToken()) &
                    Main.currentPieceLocations[new CoorPair(this.getXCoor() + 60, this.getYCoor()).getToken()] == null &
                    Main.currentPieceLocations[new CoorPair(this.getXCoor() + 120, this.getYCoor()).getToken()] == null) {
                legalMoves.add(new CoorPair(this.getXCoor() + 120, this.getYCoor()).getToken());
            }

            if (leftRookCanCastle &
                    !opponentMoves.contains(new CoorPair(this.getXCoor() - 60, this.getYCoor()).getToken()) &
                    !opponentMoves.contains(new CoorPair(this.getXCoor() - 120, this.getYCoor()).getToken()) &
                    Main.currentPieceLocations[new CoorPair(this.getXCoor() - 60, this.getYCoor()).getToken()] == null &
                    Main.currentPieceLocations[new CoorPair(this.getXCoor() - 120, this.getYCoor()).getToken()] == null &
                    Main.currentPieceLocations[new CoorPair(this.getXCoor() - 180, this.getYCoor()).getToken()] == null) {
                legalMoves.add(new CoorPair(this.getXCoor() - 120, this.getYCoor()).getToken());
            }
        }

        return legalMoves;
    }

    @Override
    public ArrayList<Integer> movesForCheck() {
        Integer newMove;
        ArrayList<Integer> potentialMoves = new ArrayList<>();

        //Possible movements the king can make
        int[][] movements = {
                {0, -60}, {-60, 0}, {0, 60}, {60, 0},
                {60, -60}, {-60, -60}, {-60, 60}, {60, 60}
        };

        //Check all movements the king can make
        for (int[] movement : movements) {
            newMove = CoorPair.tokenize(this.getXCoor() + movement[0], this.getYCoor() + movement[1]);
            if (checkPotentialMoveForCheck(newMove)) {
                potentialMoves.add(newMove);
            }
        }

        return potentialMoves;
    }

    private boolean checkPotentialMoveForCheck(Integer newMove) {
        if (CoorPair.reverseToken(newMove).isInBounds()) {
            //Return true if no pieces are at this space
            if ( Main.currentPieceLocations[newMove] == null) return true;

            return Main.currentPieceLocations[newMove].color == this.color;
        }
        return false;
    }

    /**
     * Finds the POTENTIAL moves that a king can take
     * Includes logic for taking pieces, and spaces with same color piece on them
     *
     * @return List of potential moves
     */
    public ArrayList<Integer> getPotentialMoves() {
        Integer newMove;
        ArrayList<Integer> potentialMoves = new ArrayList<>();

        //Possible movements the king can make
        int[][] movements = {
                {0, -60}, {-60, 0}, {0, 60}, {60, 0},
                {60, -60}, {-60, -60}, {-60, 60}, {60, 60}
        };

        //Check all movements the king can make
        for (int[] movement : movements) {
            newMove = CoorPair.tokenize(this.getXCoor() + movement[0], this.getYCoor() + movement[1]);
            if (checkNewMove(newMove)) {
                potentialMoves.add(newMove);
            }
        }

        return potentialMoves;
    }

    /**
     * Checks if a new move is valid
     *
     * @param newMove move player wants to make
     * @return if it's valid
     */
    private boolean checkNewMove(Integer newMove) {
        if (CoorPair.reverseToken(newMove).isInBounds()) {
            //If space is empty return true
            if (Main.currentPieceLocations[newMove] == null) return true;

            return Main.currentPieceLocations[newMove].color != this.color;
        }

        return false;
    }

    /**
     * Finds all moves that opponent can make
     *
     * @return Hashed Coordinates of all moves
     */
    public HashSet<Integer> spacesOpponentCanMove() {
        HashSet<Integer> hashedCoordinates = new HashSet<>();

        for (Piece piece : (this.color == Color.WHITE) ? Main.blackPieces : Main.whitePieces) {
            hashedCoordinates.addAll(piece.movesForCheck());
        }

        return hashedCoordinates;
    }
}
