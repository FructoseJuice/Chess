import java.io.FileNotFoundException;
import java.util.ArrayList;

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
    private void enPassantCheck(CoorPair move, ArrayList<Integer> legalMoves) {
        if (Main.currentPieceLocations[move.getToken()] != null) {
            if (Main.currentPieceLocations[move.getToken()] instanceof Pawn
                    && ((Pawn) Main.currentPieceLocations[move.getToken()]).enPassantable) {
                move.setYCoor(move.getYCoor() + ((this.color == Color.WHITE) ? -60.0 : 60.0));
                legalMoves.add(move.getToken());
            }
        }
    }

    @Override
    public ArrayList<Integer> findPotentialMoves() {
        if (!this.getCoordinates().isInBounds()) return new ArrayList<>();

        int newMove;
        int xMovement = 60;
        int yMovement = (this.color == Color.BLACK) ? 60 : -60;
        ArrayList<Integer> legalMoves = new ArrayList<>();

        //Check if we can move forward
        newMove = CoorPair.tokenize(this.getXCoor(), this.getYCoor() + yMovement);

        //If there isn't a piece in front of us, we can move forwards
        if (Main.currentPieceLocations[newMove] == null) {
            legalMoves.add(newMove);
        }

        //Checks if we can move two spaces
        if (firstMove & legalMoves.size() != 0) {

            newMove = CoorPair.tokenize(this.getXCoor(), this.getYCoor() + yMovement * 2);

            //Check if any pieces are 2 squares in front of us
            if (Main.currentPieceLocations[newMove] == null) {
                legalMoves.add(newMove);
            }
        }

        //Check if we can en-passant
        if ((this.color == Color.WHITE && this.getYCoor() == 180.0)
                || (this.color == Color.BLACK && this.getYCoor() == 240.0)) {

            enPassantCheck(new CoorPair(this.getXCoor() - 60, this.getYCoor()), legalMoves);
            enPassantCheck(new CoorPair(this.getXCoor() + 60, this.getYCoor()), legalMoves);
        }

        // Checks if there's a piece to our diagonals
        // Checks left diagonal
        int leftDiagonal = CoorPair.tokenize(this.getXCoor() - xMovement, this.getYCoor() + yMovement);
        if (IsOpponentPiece(leftDiagonal)) {
            legalMoves.add(leftDiagonal);
        }

        // Checks right diagonal
        int rightDiagonal = CoorPair.tokenize(this.getXCoor() + xMovement, this.getYCoor() + yMovement);
        if (IsOpponentPiece(rightDiagonal)) {
            legalMoves.add(rightDiagonal);
        }

        return legalMoves;
    }

    /**
     * Returns moves this pawn would make to capture a piece
     * Used by king for checking positions with check
     */
    @Override
    public ArrayList<Integer> movesForCheck() {
        if (!this.getCoordinates().isInBounds()) return new ArrayList<>();

        ArrayList<Integer> movesForCheck = new ArrayList<>();
        int yMovement = (this.color == Color.BLACK) ? 60 : -60;

        //Check each diagonal
        movesForCheck.add(CoorPair.tokenize(this.getXCoor() - 60, this.getYCoor() + yMovement));
        movesForCheck.add(CoorPair.tokenize(this.getXCoor() + 60, this.getYCoor() + yMovement));

        return movesForCheck;
    }
}
