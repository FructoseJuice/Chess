import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(Color color) throws FileNotFoundException {
        super(PieceType.KNIGHT, color);
    }

    @Override
    public ArrayList<CoorPair> findLegalMoves() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<CoorPair> legalMoves = new ArrayList<>();

        //Checks up 2 right 1 movement
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() - 120);
        if (checkNewMove(newMove)) {
            legalMoves.add(new CoorPair(newMove));
        }

        //Checks up 1 right 2 movement
        newMove.setCoordinates(this.getXCoor() + 120, this.getYCoor() - 60);
        if (checkNewMove(newMove)) {
            legalMoves.add(new CoorPair(newMove));
        }

        //Checks up 2 left 1 movement
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() - 120);
        if (checkNewMove(newMove)) {
            legalMoves.add(new CoorPair(newMove));
        }

        //Checks up 1 left 2 movement
        newMove.setCoordinates(this.getXCoor() - 120, this.getYCoor() - 60);
        if (checkNewMove(newMove)) {
            legalMoves.add(new CoorPair(newMove));
        }

        //Checks down 2 right 1 movement
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() + 120);
        if (checkNewMove(newMove)) {
            legalMoves.add(new CoorPair(newMove));
        }

        //Checks down 1 right 2 movement
        newMove.setCoordinates(this.getXCoor() + 120, this.getYCoor() + 60);
        if (checkNewMove(newMove)) {
            legalMoves.add(new CoorPair(newMove));
        }

        //Checks down 2 left 1 movement
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() + 120);
        if (checkNewMove(newMove)) {
            legalMoves.add(new CoorPair(newMove));
        }

        //Check down 1 left 2 movement
        newMove.setCoordinates(this.getXCoor() - 120, this.getYCoor() + 60);
        if (checkNewMove(newMove)) {
            legalMoves.add(new CoorPair(newMove));
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
            if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                return Main.currentPieceLocations.get(newMove.hashCode()) != this.color;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<CoorPair> movesForCheck() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<CoorPair> potentialMoves = new ArrayList<>();

        //Checks up 2 right 1 movement
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() - 120);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }

        //Checks up 1 right 2 movement
        newMove.setCoordinates(this.getXCoor() + 120, this.getYCoor() - 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }

        //Checks up 2 left 1 movement
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() - 120);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }

        //Checks up 1 left 2 movement
        newMove.setCoordinates(this.getXCoor() - 120, this.getYCoor() - 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }

        //Checks down 2 right 1 movement
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() + 120);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }

        //Checks down 1 right 2 movement
        newMove.setCoordinates(this.getXCoor() + 120, this.getYCoor() + 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }

        //Checks down 2 left 1 movement
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() + 120);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }

        //Check down 1 left 2 movement
        newMove.setCoordinates(this.getXCoor() - 120, this.getYCoor() + 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(new CoorPair(newMove));
        }

        return potentialMoves;
    }

    private boolean checkPotentialMoveForCheck(CoorPair newMove) {
        if (newMove.isInBounds()) {
            if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                return Main.currentPieceLocations.get(newMove.hashCode()) == this.color;
            } else {
                return true;
            }
        }
        return false;
    }
}
