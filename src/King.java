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

    public boolean isFirstMove() {
        return firstMove;
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
        //Can only castle if it's our first move
        if (!firstMove) return legalMovesBitBoard;

        //Cannot castle out of check
        if (Main.isKingInCheck(color)) return legalMovesBitBoard;

        boolean rightRookCanCastle = false;
        boolean leftRookCanCastle = false;
        for (Piece piece : Board.pieces.getPiecesByColor(this.color)) {
            //Ensure piece is rook and if it's first move
            if (piece.pieceType != PieceType.ROOK) continue;
            if (!((Rook) piece).getFirstMoveStatus()) continue;

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

        //CHECK RIGHT SIDE CASTLE
        boolean opponentBlockingCastle =
                (BitBoard.compareToken(opponentMoves, new CoorPair(getXCoor() + 60, getYCoor()).getToken()) ||
                        (BitBoard.compareToken(opponentMoves, new CoorPair(getXCoor() + 120, getYCoor()).getToken())));

        boolean castlingSpacesEmpty =
                !Board.isSpaceOccupied(new CoorPair(getXCoor() + 60, getYCoor())) &&
                        !Board.isSpaceOccupied(new CoorPair(getXCoor() + 120, getYCoor()));


        if (rightRookCanCastle & !opponentBlockingCastle & castlingSpacesEmpty) {
            legalMovesBitBoard |= 1L << new CoorPair(this.getXCoor() + 120, this.getYCoor()).getToken();
        }

        //CHECK LEFT SIDE CASTLE
        opponentBlockingCastle =
                (BitBoard.compareToken(opponentMoves, new CoorPair(this.getXCoor() - 60, this.getYCoor()).getToken())) ||
                        (BitBoard.compareToken(opponentMoves, new CoorPair(this.getXCoor() - 120, this.getYCoor()).getToken()));

        castlingSpacesEmpty =
                !Board.isSpaceOccupied(new CoorPair(getXCoor() - 60, getYCoor())) &&
                        !Board.isSpaceOccupied(new CoorPair(getXCoor() - 120, getYCoor())) &&
                        !Board.isSpaceOccupied(new CoorPair(getXCoor() - 180, getYCoor()));

        if (leftRookCanCastle & !opponentBlockingCastle & castlingSpacesEmpty) {
            legalMovesBitBoard |= 1L << new CoorPair(getXCoor() - 120, getYCoor()).getToken();
        }


        return legalMovesBitBoard;
    }

    @Override
    public Long movesForCheck() {
        CoorPair newMove;
        long potentialMovesBitBoard = 0L;

        //Possible movements the king can make
        int[][] movements = {
                {0, -60}, {-60, 0}, {0, 60}, {60, 0},
                {60, -60}, {-60, -60}, {-60, 60}, {60, 60}
        };

        //Check all movements the king can make
        for (int[] movement : movements) {
            newMove = new CoorPair(this.getXCoor() + movement[0], this.getYCoor() + movement[1]);

            if (checkPotentialMoveForCheck(newMove)) {
                potentialMovesBitBoard = BitBoard.add(potentialMovesBitBoard, newMove.getToken());
            }
        }

        return potentialMovesBitBoard;
    }

    private boolean checkPotentialMoveForCheck(CoorPair newMove) {
        //Check if move is out of bounds
        if (!newMove.isInBounds()) return false;

        //Return true if no pieces are at this space
        if (!Board.isSpaceOccupied(newMove)) return true;

        //Return if king is protecting a piece
        return Board.currentPieceLocations[newMove.getToken()].color == this.color;
    }

    /**
     * Finds the POTENTIAL moves that a king can take
     * Includes logic for taking pieces, and spaces with same color piece on them
     *
     * @return bitboard of potential moves
     */
    public Long getPotentialMoves() {
        CoorPair newMove;
        long potentialMovesBitBoard = 0L;

        //Possible movements the king can make
        int[][] movements = {
                {0, -60}, {-60, 0}, {0, 60}, {60, 0},
                {60, -60}, {-60, -60}, {-60, 60}, {60, 60}
        };

        //Check all movements the king can make
        for (int[] movement : movements) {
            newMove = new CoorPair(getXCoor() + movement[0], getYCoor() + movement[1]);

            if (checkNewMove(newMove)) {
                potentialMovesBitBoard = BitBoard.add(potentialMovesBitBoard, newMove.getToken());
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
    private boolean checkNewMove(CoorPair newMove) {
        //Return false if move out of bounds
        if (!newMove.isInBounds()) return false;

        //If space is empty return true
        if (!Board.isSpaceOccupied(newMove)) return true;

        //Return if the king is capturing a piece
        return Board.currentPieceLocations[newMove.getToken()].color != this.color;
    }

    /**
     * Finds all moves that opponent can make
     *
     * @return Hashed Coordinates of all moves
     */
    public Long spacesOpponentCanMove() {
        long opponentMovesBitBoard = 0L;

        for (Piece piece : Board.pieces.getPiecesByColor(Main.getOpponentColor(this.color))) {
            opponentMovesBitBoard = BitBoard.merge(opponentMovesBitBoard, piece.movesForCheck());
        }

        return opponentMovesBitBoard;
    }
}
