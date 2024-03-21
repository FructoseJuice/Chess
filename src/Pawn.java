import Utils.BitBoard;
import Utils.CoorPair;

import java.io.FileNotFoundException;

public class Pawn extends Piece {
    private boolean firstMove = true;
    public boolean enPassantable = false;

    public Pawn(Color color) throws FileNotFoundException {
        super(PieceType.PAWN, color);
    }

    /**
     * Sets our first move flag to false, and checks if any opponent
     * pawns can en-passant this piece.
     */
    public void setFirstMoveFalse() {
        //Check if en-passantable
        if (firstMove) {
            if (this.color == Color.WHITE) {
                if (this.getYCoor() == 240.0) {
                    enPassantable = true;
                }
            } else {
                if (this.getYCoor() == 180.0) {
                    enPassantable = true;
                }
            }
        }

        firstMove = false;
    }

    /**
     * Check if this move en-passants
     *
     * @param move       move to check
     * @param legalMoves running list of potentially legal moves
     */
    private Long enPassantCheck(CoorPair move, Long legalMoves) {
        int moveToken = move.getToken();

        if (Board.currentPieceLocations[moveToken] != null) {
            if (Board.currentPieceLocations[moveToken] instanceof Pawn
                    && ((Pawn) Board.currentPieceLocations[moveToken]).enPassantable) {
                move.setYCoor(move.getYCoor() + ((this.color == Color.WHITE) ? -60.0 : 60.0));
                //Store new move
                legalMoves = BitBoard.add(legalMoves, move.getToken());
            }
        }

        return legalMoves;
    }

    @Override
    public Long findPotentialMoves() {
        int newMove;
        int xMovement = 60;
        int yMovement = (this.color == Color.BLACK) ? 60 : -60;
        long legalMovesBitBoard = 0L;

        /*
        Check if we can move forward
         */
        newMove = CoorPair.tokenize(this.getXCoor(), this.getYCoor() + yMovement);

        //If there isn't a piece in front of us, we can move forwards
        if (Board.currentPieceLocations[newMove] == null) {
            legalMovesBitBoard = BitBoard.add(legalMovesBitBoard, newMove);
        }

        /*
        Checks if we can move two spaces
         */
        if (firstMove & (legalMovesBitBoard != 0)) {

            newMove = CoorPair.tokenize(this.getXCoor(), this.getYCoor() + yMovement * 2);

            //Check if any pieces are 2 squares in front of us
            if (Board.currentPieceLocations[newMove] == null) {
                legalMovesBitBoard = BitBoard.add(legalMovesBitBoard, newMove);
            }
        }

        /*
        Check if we can en-passant
         */
        if ((this.color == Color.WHITE && this.getYCoor() == 180.0)
                || (this.color == Color.BLACK && this.getYCoor() == 240.0)) {

            legalMovesBitBoard |= enPassantCheck(new CoorPair(this.getXCoor() - 60, this.getYCoor()), legalMovesBitBoard);
            legalMovesBitBoard |= enPassantCheck(new CoorPair(this.getXCoor() + 60, this.getYCoor()), legalMovesBitBoard);
        }

        /*
         Checks if there's a piece to our diagonals
         */
        // Checks left diagonal
        CoorPair diagonalMove;
        diagonalMove = new CoorPair(this.getXCoor() - xMovement, this.getYCoor() + yMovement);
        if ( diagonalMove.isInBounds() ) {
            int leftDiagonal = diagonalMove.getToken();
            if (isPieceAtCoordinates(leftDiagonal)) {
                //Add if enemy at diagonal
                legalMovesBitBoard = BitBoard.add(legalMovesBitBoard, leftDiagonal);
            }
        }

        // Checks right diagonal
        diagonalMove = new CoorPair(this.getXCoor() + xMovement, this.getYCoor() + yMovement);
        if ( diagonalMove.isInBounds() ) {
            int rightDiagonal = diagonalMove.getToken();
            if (isPieceAtCoordinates(rightDiagonal)) {
                //Add if enemy at diagonal
                legalMovesBitBoard = BitBoard.add(legalMovesBitBoard, rightDiagonal);
            }
        }

        return legalMovesBitBoard;
    }

    /**
     * Returns moves this pawn would make to capture a piece
     * Used by king for checking positions with check
     */
    @Override
    public Long movesForCheck() {
        long movesForCheckBitBoard = 0L;
        int yMovement = (this.color == Color.BLACK) ? 60 : -60;

        //Check each diagonal
        CoorPair diagonalMove;
        diagonalMove = new CoorPair(this.getXCoor() - 60, this.getYCoor() + yMovement);
        if ( diagonalMove.isInBounds() ) {
            movesForCheckBitBoard = BitBoard.add(movesForCheckBitBoard, diagonalMove.getToken());
        }

        diagonalMove = new CoorPair(this.getXCoor() + 60, this.getYCoor() + yMovement);
        if ( diagonalMove.isInBounds() ) {
            movesForCheckBitBoard = BitBoard.add(movesForCheckBitBoard, diagonalMove.getToken());
        }

        return movesForCheckBitBoard;
    }
}
