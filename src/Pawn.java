import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Pawn extends Piece {
    private boolean firstMove = true;

    public Pawn(Color color) throws FileNotFoundException {
        super(PieceType.PAWN, color);
    }

    public void setFirstMoveFalse() {
        firstMove = false;
    }

    @Override
    public ArrayList<CoorPair> findLegalMoves() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<CoorPair> legalMoves = new ArrayList<>();

        //Checks if we can move forward
        if (this.color == Color.BLACK) {
            newMove.setCoordinates(this.getXCoor(), this.getYCoor() + 60);
        } else {
            newMove.setCoordinates(this.getXCoor(), this.getYCoor() - 60);
        }
        if (!Main.currentPieceLocations.containsKey(newMove.hashCode())) {
            legalMoves.add(new CoorPair(newMove));
        }

        //Checks if we can move two spaces
        if (firstMove & legalMoves.size() != 0) {
            if (this.color == Color.BLACK) {
                legalMoves.add(new CoorPair(this.getXCoor(), this.getYCoor() + 120));
            } else {
                legalMoves.add(new CoorPair(this.getXCoor(), this.getYCoor() - 120));
            }
        }

        //Checks if there's a piece to our diagonals
        if (this.color == Color.BLACK) {
            //Checks left diagonal (up left)
            newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() + 60);
            if (Main.currentPieceLocations.containsKey(newMove.hashCode()) &
                    Main.currentPieceLocations.get(newMove.hashCode()) == Color.WHITE) {
                legalMoves.add(new CoorPair(newMove));
            }
            //Checks right diagonal (up right)
            newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() + 60);
            if (Main.currentPieceLocations.containsKey(newMove.hashCode()) &
                    Main.currentPieceLocations.get(newMove.hashCode()) == Color.WHITE) {
                legalMoves.add(new CoorPair(newMove));
            }
        } else {
            //Checks left diagonal (down left)
            newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() - 60);
            if (Main.currentPieceLocations.containsKey(newMove.hashCode()) &
                    Main.currentPieceLocations.get(newMove.hashCode()) == Color.BLACK) {
                legalMoves.add(new CoorPair(newMove));
            }
            //Checks right diagonal (down right)
            newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() - 60);
            if (Main.currentPieceLocations.containsKey(newMove.hashCode()) &
                    Main.currentPieceLocations.get(newMove.hashCode()) == Color.BLACK) {
                legalMoves.add(new CoorPair(newMove));
            }
        }

        return legalMoves;
    }

    /**
     * Returns moves this pawn would make to capture a piece
     *
     * Used by king for checking positions with check
     */
    @Override
    public ArrayList<CoorPair> movesForCheck() {
        ArrayList<CoorPair> movesForCheck = new ArrayList<>();

        if (this.color == Color.BLACK) {
            movesForCheck.add(new CoorPair(this.getXCoor() - 60, this.getYCoor() + 60));
            movesForCheck.add(new CoorPair(this.getXCoor() + 60, this.getYCoor() + 60));
        } else {
            movesForCheck.add(new CoorPair(this.getXCoor() - 60, this.getYCoor() - 60));
            movesForCheck.add(new CoorPair(this.getXCoor() + 60, this.getYCoor() - 60));
        }
        return movesForCheck;
    }
}
