import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Pawn extends Piece {
    private boolean firstMove = true;
    public boolean enPassantable = false;

    public Pawn(Color color) throws FileNotFoundException {
        super(PieceType.PAWN, color);
    }

    public void setFirstMoveFalse() {
        //Check if en-passantable
        if ( firstMove ) {
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
     * @param move move to check
     * @param legalMoves running list of potentially legal moves
     */
    private void enPassantCheck(CoorPair move, ArrayList<Integer> legalMoves) {
        if ( Main.currentPieceLocations[move.getToken()] != null ) {
            if ( Main.currentPieceLocations[move.getToken()] instanceof Pawn
                    && ((Pawn) Main.currentPieceLocations[move.getToken()]).enPassantable ) {
                move.setyCoor(move.getyCoor() + ((this.color == Color.WHITE) ? -60.0 : 60.0));
                legalMoves.add(move.getToken());
            }
        }
    }

    @Override
    public ArrayList<Integer> findPotentialMoves() {
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();

        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<Integer> legalMoves = new ArrayList<>();

        //Checks if we can move forward
        if (this.color == Color.BLACK) {
            newMove.setCoordinates(this.getXCoor(), this.getYCoor() + 60);
        } else {
            newMove.setCoordinates(this.getXCoor(), this.getYCoor() - 60);
        }

        //If there isn't a piece in front of us, we can move forwards
        if (Main.currentPieceLocations[newMove.getToken()] == null) {
            legalMoves.add(newMove.getToken());
        }

        //Checks if we can move two spaces
        if (firstMove & legalMoves.size() != 0) {
            if (this.color == Color.BLACK) {
                legalMoves.add(new CoorPair(this.getXCoor(), this.getYCoor() + 120).getToken());
            } else {
                legalMoves.add(new CoorPair(this.getXCoor(), this.getYCoor() - 120).getToken());
            }
        }

        //Check if we can en-passant
        if (    ( this.color == Color.WHITE && this.getYCoor() == 180.0 )
             || ( this.color == Color.BLACK && this.getYCoor() == 240.0 ) ) {

                newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor());
                enPassantCheck(newMove, legalMoves);

                newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor());
                enPassantCheck(newMove, legalMoves);
        }

        //Checks if there's a piece to our diagonals
        if (this.color == Color.BLACK) {

            //Checks left diagonal (up left)
            newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() + 60);
            if (Main.currentPieceLocations[newMove.getToken()] != null &&
                    Main.currentPieceLocations[newMove.getToken()].color == Color.WHITE) {

                legalMoves.add(newMove.getToken());
            }

            //Checks right diagonal (up right)
            newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() + 60);
            if (Main.currentPieceLocations[newMove.getToken()] != null &&
                    Main.currentPieceLocations[newMove.getToken()].color == Color.WHITE) {
                legalMoves.add(newMove.getToken());
            }

        } else {

            //Checks left diagonal (down left)
            newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() - 60);
            if (Main.currentPieceLocations[newMove.getToken()] != null &&
                    Main.currentPieceLocations[newMove.getToken()].color == Color.BLACK) {
                legalMoves.add(newMove.getToken());
            }

            //Checks right diagonal (down right)
            newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() - 60);
            if (Main.currentPieceLocations[newMove.getToken()] != null &&
                    Main.currentPieceLocations[newMove.getToken()].color == Color.BLACK) {
                legalMoves.add(newMove.getToken());
            }
        }

        return legalMoves;
    }

    /**
     * Returns moves this pawn would make to capture a piece
     * Used by king for checking positions with check
     */
    @Override
    public ArrayList<Integer> movesForCheck() {
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();

        ArrayList<Integer> movesForCheck = new ArrayList<>();

        if (this.color == Color.BLACK) {
            movesForCheck.add(new CoorPair(this.getXCoor() - 60, this.getYCoor() + 60).getToken());
            movesForCheck.add(new CoorPair(this.getXCoor() + 60, this.getYCoor() + 60).getToken());
        } else {
            movesForCheck.add(new CoorPair(this.getXCoor() - 60, this.getYCoor() - 60).getToken());
            movesForCheck.add(new CoorPair(this.getXCoor() + 60, this.getYCoor() - 60).getToken());
        }
        return movesForCheck;
    }
}
