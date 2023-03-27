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
    public ArrayList<CoorPair> findPotentialMoves() {
        //Gets the potential legal moves this king can make
        ArrayList<CoorPair> legalMoves = getPotentialMoves();
        //Gets all possible moves of the opponent
        HashSet<Integer> opponentMoves = spacesOpponentCanMove();
        //If one of our potential moves is the same as an opponents possible move, then it's not legal as it
        //Puts the king in check
        for (int i = 0; i < legalMoves.size(); i++) {
            if (opponentMoves.contains(legalMoves.get(i).getToken())) {
                legalMoves.remove(i);
                --i;
            }
        }

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
            we're not castling THROUGH, or INTO check.
             */
            if (rightRookCanCastle &
                    !opponentMoves.contains(new CoorPair(this.getXCoor() + 60, this.getYCoor()).getToken()) &
                    !opponentMoves.contains(new CoorPair(this.getXCoor() + 120, this.getYCoor()).getToken()) &
                    !Main.currentPieceLocations.containsKey(new CoorPair(this.getXCoor() + 60, this.getYCoor()).getToken()) &
                    !Main.currentPieceLocations.containsKey(new CoorPair(this.getXCoor() + 120, this.getYCoor()).getToken())) {
                legalMoves.add(new CoorPair(this.getXCoor() + 120, this.getYCoor()));
            }

            if (leftRookCanCastle &
                    !opponentMoves.contains(new CoorPair(this.getXCoor() - 60, this.getYCoor()).getToken()) &
                    !opponentMoves.contains(new CoorPair(this.getXCoor() - 120, this.getYCoor()).getToken()) &
                    !Main.currentPieceLocations.containsKey(new CoorPair(this.getXCoor() - 60, this.getYCoor()).getToken()) &
                    !Main.currentPieceLocations.containsKey(new CoorPair(this.getXCoor() - 120, this.getYCoor()).getToken()) &
                    !Main.currentPieceLocations.containsKey(new CoorPair(this.getXCoor() - 180, this.getYCoor()).getToken())) {
                legalMoves.add(new CoorPair(this.getXCoor() - 120, this.getYCoor()));
            }
        }

        return legalMoves;
    }

    @Override
    public ArrayList<CoorPair> movesForCheck() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<CoorPair> potentialMoves = new ArrayList<>();

        //Check moving up
        newMove.setCoordinates(this.getXCoor(), this.getYCoor() - 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move left
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor());
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move down
        newMove.setCoordinates(this.getXCoor(), this.getYCoor() + 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move right
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor());
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move up right
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() - 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move up left
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() - 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move down left
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() + 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move down right
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() + 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }

        return potentialMoves;
    }

    private boolean checkPotentialMoveForCheck(CoorPair newMove) {
        if (newMove.isInBounds()) {
            if (Main.currentPieceLocations.containsKey(newMove.getToken())) {
                return Main.currentPieceLocations.get(newMove.getToken()).color == this.color;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the POTENTIAL moves that a king can take
     * Includes logic for taking pieces, and spaces with same color piece on them
     *
     * @return List of potential moves
     */
    public ArrayList<CoorPair> getPotentialMoves() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<CoorPair> potentialMoves = new ArrayList<>();

        //Check moving up
        newMove.setCoordinates(this.getXCoor(), this.getYCoor() - 60);
        if (checkNewMove(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move left
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor());
        if (checkNewMove(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move down
        newMove.setCoordinates(this.getXCoor(), this.getYCoor() + 60);
        if (checkNewMove(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move right
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor());
        if (checkNewMove(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move up right
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() - 60);
        if (checkNewMove(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move up left
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() - 60);
        if (checkNewMove(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move down left
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() + 60);
        if (checkNewMove(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }
        //Check move down right
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() + 60);
        if (checkNewMove(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }

        return potentialMoves;
    }

    /**
     * Checks if a new move is valid
     *
     * @param newMove move player wants to make
     * @return if it's valid
     */
    private boolean checkNewMove(CoorPair newMove) {
        if (newMove.isInBounds()) {
            if (Main.currentPieceLocations.containsKey(newMove.getToken())) {
                return Main.currentPieceLocations.get(newMove.getToken()).color != this.color;
            } else {
                return true;
            }
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
            for (CoorPair potentialMove : piece.movesForCheck()) {
                hashedCoordinates.add(potentialMove.getToken());
            }
        }

        return hashedCoordinates;
    }
}
