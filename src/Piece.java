import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Represents all pieces on the chess board
 */
public abstract class Piece {
    protected CoorPair coorPair;
    final protected Color color;
    final protected PieceType pieceType;
    public final ImageView pieceObject;

    public enum Color {WHITE, BLACK}

    public enum PieceType {
        KING,
        QUEEN,
        ROOK,
        BISHOP,
        KNIGHT,
        PAWN
    }

    public Piece(PieceType pieceType, Color color) throws FileNotFoundException {
        this.pieceType = pieceType;
        this.color = color;

        //Constructs path to png for respective piece
        String imagePath = "images\\Pieces\\";
        if (color == Color.WHITE) {
            imagePath += "White_";
        } else {
            imagePath += "Black_";
        }

        switch (pieceType) {
            case PAWN -> imagePath += "Pawn.png";
            case BISHOP -> imagePath += "Bishop.png";
            case KNIGHT -> imagePath += "Knight.png";
            case ROOK -> imagePath += "Rook.png";
            case QUEEN -> imagePath += "Queen.png";
            case KING -> imagePath += "King.png";
        }

        Image piece = new Image(new FileInputStream(imagePath));
        //Sets image view for piece
        this.pieceObject = new ImageView(piece);
    }

    /**
     * Required for all piece classes to override this method.
     * Finds the potential moves that this piece can take
     */
    public abstract ArrayList<Integer> findPotentialMoves();

    /**
     * Required for all piece classes to override this method.
     * Finds the potential moves that this piece can take to put a king in check
     * Includes protecting own pieces.
     */
    public abstract ArrayList<Integer> movesForCheck();

    public void setCoordinates(double xCoor, double yCoor) {
        //Set new coordinates
        coorPair = new CoorPair(xCoor, yCoor);

        //Draw to screen
        draw();
    }

    public void setCoordinates(CoorPair newCoors) {
        coorPair = new CoorPair(newCoors.getXCoor(), newCoors.getYCoor());
    }

    public void setCoordinates(Integer coordinateToken) {
        coorPair = CoorPair.reverseToken(coordinateToken);
    }

    //Returns in format:
    //double[XCoor, YCoor]
    public CoorPair getCoordinates() {
        return coorPair;
    }

    public double getXCoor() {
        return coorPair.getXCoor();
    }

    public double getYCoor() {
        return coorPair.getYCoor();
    }

    public void setXCoor(double XCoor) {
        coorPair.setXCoor(XCoor);
    }

    public void setYCoor(double YCoor) {
        coorPair.setYCoor(YCoor);
    }

    public void draw() {
        pieceObject.setLayoutX(coorPair.getXCoor());
        pieceObject.setLayoutY(coorPair.getYCoor());
    }

    /**
     * Finds the nearest space for snapping piece to grid
     */
    public void findNearestSpace() {
        int quotient;

        //Finding closest x coordinate
        int xCoor = (int) coorPair.getXCoor();

        if (xCoor < 30) {
            coorPair.setXCoor(0);
        } else {
            quotient = Math.floorDiv(xCoor, 60);
            //Find remainder and determine closest square
            if (xCoor % 60 >= 30) quotient++;

            coorPair.setXCoor(quotient * 60.0);
        }


        //Finding closest y coordinate
        int yCoor = (int) coorPair.getYCoor();

        if ( yCoor < 30 ) {
            coorPair.setYCoor(0);
        } else {
            quotient = Math.floorDiv(yCoor, 60);

            if (yCoor % 60 >= 30) quotient++;

            coorPair.setYCoor(quotient * 60.0);
        }
    }


    /**
     * Finds all of a pieces potential moves in all diagonal directions
     * FOR USE BY BISHOPS AND QUEENS
     *
     * @return list of hashed potential moves in diagonal directions
     */
    public ArrayList<Integer> findPotentialDiagonalMoves() {
        ArrayList<Integer> legalMoves = new ArrayList<>();

        int[][] directions = {
                {1, -1},  // up-right
                {-1, -1}, // up-left
                {-1, 1},  // down-left
                {1, 1}    // down-right
        };

        for (int[] direction : directions) {
            int dx = direction[0]; //X coordinate movement
            int dy = direction[1]; //Y coordinate movement

            for (int j = 1; j < 8; j++) {
                int newX = (int) (this.getXCoor() + (j * 60 * dx));
                int newY = (int) (this.getYCoor() + (j * 60 * dy));

                CoorPair newMove = new CoorPair(newX, newY);

                //Check if move is in bounds
                if (!newMove.isInBounds()) {
                    break;
                }

                int newMoveToken = newMove.getToken();
                Piece pieceAtLocation = Main.currentPieceLocations[newMoveToken];

                //Check if there is a piece at this location
                if (pieceAtLocation != null) {
                    //Check if this piece is of the opposite color
                    if (pieceAtLocation.color != this.color) {
                        legalMoves.add(newMoveToken);
                    }
                    break;
                }

                legalMoves.add(newMoveToken);
            }
        }

        return legalMoves;
    }

    /**
     * Finds all of a pieces potential moves in all horizontal directions
     * FOR USE BY ROOKS AND QUEENS
     *
     * @return list of hashed potential moves in horizontal directions
     */
    public ArrayList<Integer> findPotentialHorizontalMoves() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<Integer> legalMoves = new ArrayList<>();

        int[][] directions = {
                {1, 0},  //Right movement
                {-1, 0}, //Left movement
                {0, -1}, //Up movement
                {0, 1}   //Down movement
        };

        for (int[] direction : directions) {
            int dx = direction[0]; //X coordinate movement
            int dy = direction[1]; //Y coordinate movement

            for (int i = 1; i < 8; i++) {
                newMove.setCoordinates(this.getXCoor() + (i * dx * 60), this.getYCoor() + (i * dy * 60));

                if (!newMove.isInBounds()) {
                    break;  // Stop checking this direction if move is out of bounds
                }

                if (Main.currentPieceLocations[newMove.getToken()] != null) {
                    if (Main.currentPieceLocations[newMove.getToken()].color != this.color) {
                        legalMoves.add(newMove.getToken());
                    }
                    break;  // Stop checking this direction if piece is encountered
                }

                legalMoves.add(newMove.getToken());
            }
        }

        return legalMoves;
    }


    /**
     * Used for finding all potential moves for horizontal moving pieces
     * This includes pieces it's protecting
     *
     * @return Hashed potential moves of piece for calculating check
     */
    public ArrayList<Integer> horizontalForCheck() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<Integer> legalMoves = new ArrayList<>();

        int[][] directions = {
                {1, 0},  //Right movement
                {-1, 0}, //Left movement
                {0, -1}, //Up movement
                {0, 1}   //Down movement
        };

        for (int[] direction : directions) {
            int dx = direction[0]; //X coordinate movement
            int dy = direction[1]; //Y coordinate movement

            for (int i = 1; i < 8; i++) {
                newMove.setCoordinates(this.getXCoor() + (i * dx * 60), this.getYCoor() + (i * dy * 60));

                if (!newMove.isInBounds()) {
                    break;  // Stop checking this direction if move is out of bounds
                }

                if (Main.currentPieceLocations[newMove.getToken()] != null) {
                    if (Main.currentPieceLocations[newMove.getToken()].color == this.color) {
                        legalMoves.add(newMove.getToken());
                    }
                    break;  // Stop checking this direction if piece is encountered
                } else {
                    //If we encountered the opponent king, keep adding moves in this direction
                    if (pieceOpponentKing(newMove.getToken())) {
                        legalMoves.add(newMove.getToken());
                        continue;
                    }
                }

                legalMoves.add(newMove.getToken());
            }
        }

        return legalMoves;
    }

    /**
     * Used for finding all potential moves for diagonal moving pieces
     * This includes pieces it's protecting
     *
     * @return All hashed potential moves for calculating check
     */
    public ArrayList<Integer> diagonalForCheck() {
        ArrayList<Integer> legalMoves = new ArrayList<>();

        int[][] directions = {
                {1, -1},  // up-right
                {-1, -1}, // up-left
                {-1, 1},  // down-left
                {1, 1}    // down-right
        };

        for (int[] direction : directions) {
            int xDirection = direction[0];
            int yDirection = direction[1];

            for (int j = 1; j < 8; j++) {
                int newX = (int) (this.getXCoor() + (j * 60 * xDirection));
                int newY = (int) (this.getYCoor() + (j * 60 * yDirection));

                CoorPair newMove = new CoorPair(newX, newY);

                //Check if move is in bounds
                if (!newMove.isInBounds()) {
                    break;
                }

                int newMoveToken = newMove.getToken();
                Piece pieceAtLocation = Main.currentPieceLocations[newMoveToken];

                //Check if there is a piece at this location
                if (pieceAtLocation != null) {
                    //Check if this piece is of the opposite color
                    if (pieceAtLocation.color == this.color) {
                        legalMoves.add(newMoveToken);
                    }
                    break;
                } else {
                    //If encountered enemy king, keep adding moves in this direction
                    if ( pieceOpponentKing(newMoveToken)) {
                        legalMoves.add(newMoveToken);
                        continue;
                    }
                }

                legalMoves.add(newMoveToken);
            }
        }

        return legalMoves;
    }

    /**
     * For use by BISHOPS, ROOKS, AND QUEENS
     * for finding moves they could make for check
     * If they encounter a king on their movement path, we find the next potential move
     * @return if they can continue moving in this direction
     */
    private boolean pieceOpponentKing(Integer newMove) {
        //If the piece is the king, we have to keep adding moves in this direction
        if (Main.currentPieceLocations[newMove] instanceof King) {
            return (Main.currentPieceLocations[newMove].color != this.color);
        }

        return false;
    }

    /**
     * Checks if there is a piece at specified coordinates @coordinateToken, and if it's of the opposite color
     * @param coordinateToken Coordinates to check
     * @return If opponent piece at coordinates
     */
    public boolean IsOpponentPiece(int coordinateToken) {
        return (Main.currentPieceLocations[coordinateToken] != null
                && Main.currentPieceLocations[coordinateToken].color != this.color);
    }
}