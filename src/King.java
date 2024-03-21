import Utils.BitBoard;
import Utils.CoorPair;

import java.io.FileNotFoundException;

public class King extends Piece {
    private boolean firstMove = true;

    public King(Color color) throws FileNotFoundException {
        super(PieceType.KING, color);
    }

    public void setFirstMoveFalse() {
        firstMove = false;
    }

    @Override
    public Long findPotentialMoves() {

        Long legalMovesBitBoard = getPotentialMoves();

        //Gets all possible moves of the opponent
        Long opponentMoves = spacesOpponentCanMove();

        //Prune away moves that put us in check
        legalMovesBitBoard = BitBoard.prune(legalMovesBitBoard, opponentMoves);

        /*
        Checks if a king can castle
         */

        //Can only castle if it's our first move, and we're not in check
        if (firstMove && !Main.isKingInCheck(this.color)) {
            boolean rightRookCanCastle = false;
            boolean leftRookCanCastle = false;
            for (Piece piece : (this.color == Color.WHITE) ? Main.whitePieces : Main.blackPieces) {
                //Ensure piece is rook and if it's first move
                if (piece.pieceType != PieceType.ROOK) continue;
                if(!((Rook) piece).getFirstMoveStatus()) continue;

                if (piece.getCoordinates().coorEquals(new CoorPair(this.getXCoor() + 180, this.getYCoor()))) {
                    rightRookCanCastle = true;
                }
                if (piece.getCoordinates().coorEquals(new CoorPair(this.getXCoor() - 240, this.getYCoor()))) {
                    leftRookCanCastle = true;
                }
            }

            /*
            Only add castle as legal if the rook hasn't moved, and
            we're not castling through, or into check.
             */
            if (rightRookCanCastle &
                    ((opponentMoves & new CoorPair(this.getXCoor() + 60, this.getYCoor()).getToken()) == 0) &
                    ((opponentMoves & new CoorPair(this.getXCoor() + 120, this.getYCoor()).getToken()) == 0) &
                    Main.currentPieceLocations[new CoorPair(this.getXCoor() + 60, this.getYCoor()).getToken()] == null &
                    Main.currentPieceLocations[new CoorPair(this.getXCoor() + 120, this.getYCoor()).getToken()] == null) {

                legalMovesBitBoard |= 1L<<new CoorPair(this.getXCoor() + 120, this.getYCoor()).getToken();
            }

            if (leftRookCanCastle &
                    ((opponentMoves & new CoorPair(this.getXCoor() - 60, this.getYCoor()).getToken()) == 0) &
                    ((opponentMoves & new CoorPair(this.getXCoor() - 120, this.getYCoor()).getToken()) == 0) &
                    Main.currentPieceLocations[new CoorPair(this.getXCoor() - 60, this.getYCoor()).getToken()] == null &
                    Main.currentPieceLocations[new CoorPair(this.getXCoor() - 120, this.getYCoor()).getToken()] == null &
                    Main.currentPieceLocations[new CoorPair(this.getXCoor() - 180, this.getYCoor()).getToken()] == null) {

                legalMovesBitBoard |= 1L<<new CoorPair(this.getXCoor() - 120, this.getYCoor()).getToken();
            }
        }

        return legalMovesBitBoard;
    }

    @Override
    public Long movesForCheck() {
        int newMove;
        long potentialMovesBitBoard = 0L;

        //Possible movements the king can make
        int[][] movements = {
                {0, -60}, {-60, 0}, {0, 60}, {60, 0},
                {60, -60}, {-60, -60}, {-60, 60}, {60, 60}
        };

        //Check all movements the king can make
        for (int[] movement : movements) {
            newMove = CoorPair.tokenize(this.getXCoor() + movement[0], this.getYCoor() + movement[1]);
            if (checkPotentialMoveForCheck(newMove)) {
                potentialMovesBitBoard = BitBoard.add(potentialMovesBitBoard, newMove);
            }
        }

        return potentialMovesBitBoard;
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
     * @return bitboard of potential moves
     */
    public Long getPotentialMoves() {
        int newMove;
        long potentialMovesBitBoard = 0L;

        //Possible movements the king can make
        int[][] movements = {
                {0, -60}, {-60, 0}, {0, 60}, {60, 0},
                {60, -60}, {-60, -60}, {-60, 60}, {60, 60}
        };

        //Check all movements the king can make
        for (int[] movement : movements) {
            newMove = CoorPair.tokenize(this.getXCoor() + movement[0], this.getYCoor() + movement[1]);
            if (checkNewMove(newMove)) {
                potentialMovesBitBoard = BitBoard.add(potentialMovesBitBoard, newMove);
            }
        }

        return potentialMovesBitBoard;
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
    public Long spacesOpponentCanMove() {
        long opponentMovesBitBoard = 0L;

        for (Piece piece : (this.color == Color.WHITE) ? Main.blackPieces : Main.whitePieces) {
            opponentMovesBitBoard = BitBoard.merge(opponentMovesBitBoard, piece.movesForCheck());
        }

        return opponentMovesBitBoard;
    }
}
