import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(Color color) throws FileNotFoundException {
        super(PieceType.KNIGHT, color);
    }

    @Override
    public ArrayList<Integer> findPotentialMoves() {
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();

        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<Integer> legalMoves = new ArrayList<>();

        //Checks up 2 right 1 movement
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() - 120);
        if (checkNewMove(newMove)) {
            legalMoves.add(newMove.getToken());
        }

        //Checks up 1 right 2 movement
        newMove.setCoordinates(this.getXCoor() + 120, this.getYCoor() - 60);
        if (checkNewMove(newMove)) {
            legalMoves.add(newMove.getToken());
        }

        //Checks up 2 left 1 movement
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() - 120);
        if (checkNewMove(newMove)) {
            legalMoves.add(newMove.getToken());
        }

        //Checks up 1 left 2 movement
        newMove.setCoordinates(this.getXCoor() - 120, this.getYCoor() - 60);
        if (checkNewMove(newMove)) {
            legalMoves.add(newMove.getToken());
        }

        //Checks down 2 right 1 movement
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() + 120);
        if (checkNewMove(newMove)) {
            legalMoves.add(newMove.getToken());
        }

        //Checks down 1 right 2 movement
        newMove.setCoordinates(this.getXCoor() + 120, this.getYCoor() + 60);
        if (checkNewMove(newMove)) {
            legalMoves.add(newMove.getToken());
        }

        //Checks down 2 left 1 movement
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() + 120);
        if (checkNewMove(newMove)) {
            legalMoves.add(newMove.getToken());
        }

        //Check down 1 left 2 movement
        newMove.setCoordinates(this.getXCoor() - 120, this.getYCoor() + 60);
        if (checkNewMove(newMove)) {
            legalMoves.add(newMove.getToken());
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

    @Override
    public ArrayList<Integer> movesForCheck() {
        if ( !this.getCoordinates().isInBounds() ) return new ArrayList<>();

        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<Integer> potentialMoves = new ArrayList<>();

        //Checks up 2 right 1 movement
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() - 120);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(newMove.getToken());
        }

        //Checks up 1 right 2 movement
        newMove.setCoordinates(this.getXCoor() + 120, this.getYCoor() - 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(newMove.getToken());
        }

        //Checks up 2 left 1 movement
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() - 120);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(newMove.getToken());
        }

        //Checks up 1 left 2 movement
        newMove.setCoordinates(this.getXCoor() - 120, this.getYCoor() - 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(newMove.getToken());
        }

        //Checks down 2 right 1 movement
        newMove.setCoordinates(this.getXCoor() + 60, this.getYCoor() + 120);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(newMove.getToken());
        }

        //Checks down 1 right 2 movement
        newMove.setCoordinates(this.getXCoor() + 120, this.getYCoor() + 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(newMove.getToken());
        }

        //Checks down 2 left 1 movement
        newMove.setCoordinates(this.getXCoor() - 60, this.getYCoor() + 120);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(newMove.getToken());
        }

        //Check down 1 left 2 movement
        newMove.setCoordinates(this.getXCoor() - 120, this.getYCoor() + 60);
        if (checkPotentialMoveForCheck(newMove)) {
            potentialMoves.add(newMove.getToken());
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
