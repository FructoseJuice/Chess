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
    protected Color color;
    protected PieceType pieceType;
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
            case PAWN: imagePath += "Pawn.png"; break;
            case BISHOP: imagePath += "Bishop.png"; break;
            case KNIGHT: imagePath += "Knight.png"; break;
            case ROOK: imagePath += "Rook.png"; break;
            case QUEEN: imagePath += "Queen.png"; break;
            case KING: imagePath += "King.png"; break;
        }

        Image piece = new Image(new FileInputStream(imagePath));
        //Sets image view for piece
        this.pieceObject = new ImageView(piece);
    }

    /**
     * Required for all piece classes to override this method
     * Finds the legal moves that this piece can take
     */
    public abstract ArrayList<CoorPair> findLegalMoves();

    /**
     * Required for all piece classes to override this method
     * Finds the potential moves that this piece can take to put a king in check
     */
    public abstract ArrayList<CoorPair> movesForCheck();

    public void setCoordinates(double xCoor, double yCoor) {
        coorPair = new CoorPair(xCoor, yCoor);
        pieceObject.setLayoutX(xCoor);
        pieceObject.setLayoutY(yCoor);
    }

    //Returns in format:
    //double[XCoor, YCoor]
    public CoorPair getCoordinates() {
        return coorPair;
    }

    public double getXCoor() {
        return coorPair.getxCoor();
    }

    public double getYCoor() {
        return coorPair.getyCoor();
    }

    public void setXCoor(double XCoor) {
        coorPair.setxCoor(XCoor);
    }

    public void setYCoor(double YCoor) {
        coorPair.setyCoor(YCoor);
    }

    public void draw() {
        pieceObject.setLayoutX(coorPair.getxCoor());
        pieceObject.setLayoutY(coorPair.getyCoor());
    }

    /**
     * Finds the nearest space in order to snap piece to grid
     */
    public void findNearestSpace() {
        int[] possibleSpaceCoors = new int[]{0, 60, 120, 180, 240, 300, 360, 420};
        int difference;
        int closestIndex = 0;
        //Finding closest x coordinate
        difference = (int) Math.abs(possibleSpaceCoors[0] - coorPair.getxCoor());
        for (int i = 1; i < possibleSpaceCoors.length; i++) {
            int checkDifference = (int) Math.abs(possibleSpaceCoors[i] - coorPair.getxCoor());
            if (checkDifference < difference) {
                closestIndex = i;
                difference = checkDifference;
            }
        }
        coorPair.setxCoor(possibleSpaceCoors[closestIndex] * 1.0);

        //Finding closest x coordinate
        closestIndex = 0;
        difference = (int) Math.abs(possibleSpaceCoors[0] - coorPair.getyCoor());
        for (int i = 1; i < possibleSpaceCoors.length; i++) {
            int checkDifference = (int) Math.abs(possibleSpaceCoors[i] - coorPair.getyCoor());
            if (checkDifference < difference) {
                closestIndex = i;
                difference = checkDifference;
            }
        }
        coorPair.setyCoor(possibleSpaceCoors[closestIndex] * 1.0);
    }

    /**
     * Finds all of a pieces legal moves in all diagonal directions
     * FOR USE BY BISHOPS AND QUEENS
     *
     * @return list of legal moves in diagonal directions
     */
    public ArrayList<CoorPair> findLegalDiagonalMoves() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<CoorPair> legalMoves = new ArrayList<>();

        boolean canMoveUpRight = true;
        boolean canMoveUpLeft = true;
        boolean canMoveDownLeft = true;
        boolean canMoveDownRight = true;

        for (int i = 1; i < 8; i++) {

            //Checks diagonal to the up -> right
            if (canMoveUpRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color != this.color) {
                            legalMoves.add(new CoorPair(newMove));
                        }
                        canMoveUpRight = false;
                    } else {
                        legalMoves.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveUpRight = false;
                }
            }


            //Checks diagonal to the up -> left
            if (canMoveUpLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color != this.color) {
                            legalMoves.add(new CoorPair(newMove));
                        }
                        canMoveUpLeft = false;
                    } else {
                        legalMoves.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveUpLeft = false;
                }
            }


            //Checks diagonal to the down -> left
            if (canMoveDownLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color != this.color) {
                            legalMoves.add(new CoorPair(newMove));
                        }
                        canMoveDownLeft = false;
                    } else {
                        legalMoves.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveDownLeft = false;
                }
            }


            //Checks diagonal to the down -> right
            if (canMoveDownRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color != this.color) {
                            legalMoves.add(new CoorPair(newMove));
                        }
                        canMoveDownRight = false;
                    } else {
                        legalMoves.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveDownRight = false;
                }
            }
        }


        return legalMoves;
    }

    /**
     * Finds all of a pieces legal moves in all horizontal directions
     * FOR USE BY ROOKS AND QUEENS
     *
     * @return list of legal moves in horizontal directions
     */
    public ArrayList<CoorPair> findLegalHorizontalMoves() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<CoorPair> legalMoves = new ArrayList<>();

        boolean canMoveRight = true;
        boolean canMoveLeft = true;
        boolean canMoveUp = true;
        boolean canMoveDown = true;

        for (int i = 1; i < 8; i++) {

            //Checks right horizontal movement
            if (canMoveRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor());

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color != this.color) {
                            legalMoves.add(new CoorPair(newMove));
                        }
                        canMoveRight = false;
                    } else {
                        legalMoves.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveRight = false;
                }
            }

            //Checks left horizontal movement
            if (canMoveLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor());

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color != this.color) {
                            legalMoves.add(new CoorPair(newMove));
                        }
                        canMoveLeft = false;
                    } else {
                        legalMoves.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveLeft = false;
                }
            }

            //Checks for up movement
            if (canMoveUp) {
                newMove.setCoordinates(this.getXCoor(), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color != this.color) {
                            legalMoves.add(new CoorPair(newMove));
                        }
                        canMoveUp = false;
                    } else {
                        legalMoves.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveUp = false;
                }
            }

            //Checks for down movement
            if (canMoveDown) {
                newMove.setCoordinates(this.getXCoor(), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color != this.color) {
                            legalMoves.add(new CoorPair(newMove));
                        }
                        canMoveDown = false;
                    } else {
                        legalMoves.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveDown = false;
                }
            }
        }

        return legalMoves;
    }

    /**
     * Used for finding all potential moves for horizontal moving pieces
     * This includes pieces it's protecting
     *
     * @return Potential moves of piece for calculating check
     */
    public ArrayList<CoorPair> horizontalForCheck() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<CoorPair> movesForCheck = new ArrayList<>();

        boolean canMoveRight = true;
        boolean canMoveLeft = true;
        boolean canMoveUp = true;
        boolean canMoveDown = true;

        for (int i = 1; i < 8; i++) {

            //Checks right horizontal movement
            if (canMoveRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor());

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color == this.color) {
                            movesForCheck.add(new CoorPair(newMove));

                        }
                        canMoveRight = ifPieceKing(newMove);

                    } else {
                        movesForCheck.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveRight = false;
                }
            }

            //Checks left horizontal movement
            if (canMoveLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor());

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color == this.color) {
                            movesForCheck.add(new CoorPair(newMove));
                        }
                        canMoveLeft = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveLeft = false;
                }
            }

            //Checks for up movement
            if (canMoveUp) {
                newMove.setCoordinates(this.getXCoor(), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color == this.color) {
                            movesForCheck.add(new CoorPair(newMove));
                        }
                        canMoveUp = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveUp = false;
                }
            }

            //Checks for down movement
            if (canMoveDown) {
                newMove.setCoordinates(this.getXCoor(), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color == this.color) {
                            movesForCheck.add(new CoorPair(newMove));
                        }
                        canMoveDown = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveDown = false;
                }
            }
        }

        return movesForCheck;
    }

    /**
     * Used for finding all potential moves for diagonal moving pieces
     * This includes pieces it's protecting
     *
     * @return All potential moves for calculating check
     */
    public ArrayList<CoorPair> diagonalForCheck() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<CoorPair> movesForCheck = new ArrayList<>();

        boolean canMoveUpRight = true;
        boolean canMoveUpLeft = true;
        boolean canMoveDownLeft = true;
        boolean canMoveDownRight = true;

        for (int i = 1; i < 8; i++) {

            //Checks diagonal to the up -> right
            if (canMoveUpRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color == this.color) {
                            movesForCheck.add(new CoorPair(newMove));
                        }
                        canMoveUpRight = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveUpRight = false;
                }
            }


            //Checks diagonal to the up -> left
            if (canMoveUpLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color == this.color) {
                            movesForCheck.add(new CoorPair(newMove));
                        }
                        canMoveUpLeft = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveUpLeft = false;
                }
            }


            //Checks diagonal to the down -> left
            if (canMoveDownLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color == this.color) {
                            movesForCheck.add(new CoorPair(newMove));
                        }
                        canMoveDownLeft = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveDownLeft = false;
                }
            }


            //Checks diagonal to the down -> right
            if (canMoveDownRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations.containsKey(newMove.hashCode())) {
                        if (Main.currentPieceLocations.get(newMove.hashCode()).color == this.color) {
                            movesForCheck.add(new CoorPair(newMove));
                        }
                        canMoveDownRight = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(new CoorPair(newMove));
                    }
                } else {
                    canMoveDownRight = false;
                }
            }
        }


        return movesForCheck;

    }

    /**
     * For use by BISHOPS, ROOKS, AND QUEENS
     * for finding moves they could make for check
     * If they encounter a king on their movement path, we find the next potential move
     * @return if they can continue moving in this direction
     */
    private boolean ifPieceKing(CoorPair newMove) {
        //If the piece is the king, we have to keep adding moves in this direction
        return Main.currentPieceLocations.get(newMove.hashCode()) instanceof King;
    }
}