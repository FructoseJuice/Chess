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
     * Required for all piece classes to override this method
     * Finds the potential moves that this piece can take
     */
    public abstract ArrayList<Integer> findPotentialMoves();

    /**
     * Required for all piece classes to override this method
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
        coorPair = new CoorPair(newCoors.getxCoor(), newCoors.getyCoor());
    }

    public void setCoordinates(Integer coordinateToken) {
        coorPair = CoorPair.reverseHash(coordinateToken);
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
     * Finds the nearest space for snapping piece to grid
     */
    public void findNearestSpace() {
        int quotient;

        //Finding closest x coordinate
        int xCoor = (int) coorPair.getxCoor();

        if (xCoor < 30) {
            coorPair.setxCoor(0);
        } else {
            quotient = Math.floorDiv(xCoor, 60);
            //Find remainder and determine closest square
            if (xCoor % 60 >= 30) quotient++;

            coorPair.setxCoor(quotient * 60.0);
        }


        //Finding closest y coordinate
        int yCoor = (int) coorPair.getyCoor();

        if ( yCoor < 30 ) {
            coorPair.setyCoor(0);
        } else {
            quotient = Math.floorDiv(yCoor, 60);

            if (yCoor % 60 >= 30) quotient++;

            coorPair.setyCoor(quotient * 60.0);
        }
    }


    /**
     * Finds all of a pieces potential moves in all diagonal directions
     * FOR USE BY BISHOPS AND QUEENS
     *
     * @return list of hashed potential moves in diagonal directions
     */
    public ArrayList<Integer> findPotentialDiagonalMoves() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<Integer> legalMoves = new ArrayList<>();

        boolean canMoveUpRight = true;
        boolean canMoveUpLeft = true;
        boolean canMoveDownLeft = true;
        boolean canMoveDownRight = true;

        for (int i = 1; i < 8; i++) {

            //Checks diagonal to the up -> right
            if (canMoveUpRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color != this.color) {
                            legalMoves.add(newMove.getToken());
                        }
                        canMoveUpRight = false;
                    } else {
                        legalMoves.add(newMove.getToken());
                    }
                } else {
                    canMoveUpRight = false;
                }
            }


            //Checks diagonal to the up -> left
            if (canMoveUpLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color != this.color) {
                            legalMoves.add(newMove.getToken());
                        }
                        canMoveUpLeft = false;
                    } else {
                        legalMoves.add(newMove.getToken());
                    }
                } else {
                    canMoveUpLeft = false;
                }
            }


            //Checks diagonal to the down -> left
            if (canMoveDownLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color != this.color) {
                            legalMoves.add(newMove.getToken());
                        }
                        canMoveDownLeft = false;
                    } else {
                        legalMoves.add(newMove.getToken());
                    }
                } else {
                    canMoveDownLeft = false;
                }
            }


            //Checks diagonal to the down -> right
            if (canMoveDownRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color != this.color) {
                            legalMoves.add(newMove.getToken());
                        }
                        canMoveDownRight = false;
                    } else {
                        legalMoves.add(newMove.getToken());
                    }
                } else {
                    canMoveDownRight = false;
                }
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

        boolean canMoveRight = true;
        boolean canMoveLeft = true;
        boolean canMoveUp = true;
        boolean canMoveDown = true;

        for (int i = 1; i < 8; i++) {

            //Checks right horizontal movement
            if (canMoveRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor());

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color != this.color) {
                            legalMoves.add(newMove.getToken());
                        }
                        canMoveRight = false;
                    } else {
                        legalMoves.add(newMove.getToken());
                    }
                } else {
                    canMoveRight = false;
                }
            }

            //Checks left horizontal movement
            if (canMoveLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor());

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color != this.color) {
                            legalMoves.add(newMove.getToken());
                        }
                        canMoveLeft = false;
                    } else {
                        legalMoves.add(newMove.getToken());
                    }
                } else {
                    canMoveLeft = false;
                }
            }

            //Checks for up movement
            if (canMoveUp) {
                newMove.setCoordinates(this.getXCoor(), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color != this.color) {
                            legalMoves.add(newMove.getToken());
                        }
                        canMoveUp = false;
                    } else {
                        legalMoves.add(newMove.getToken());
                    }
                } else {
                    canMoveUp = false;
                }
            }

            //Checks for down movement
            if (canMoveDown) {
                newMove.setCoordinates(this.getXCoor(), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color != this.color) {
                            legalMoves.add(newMove.getToken());
                        }
                        canMoveDown = false;
                    } else {
                        legalMoves.add(newMove.getToken());
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
     * @return Hashed potential moves of piece for calculating check
     */
    public ArrayList<Integer> horizontalForCheck() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<Integer> movesForCheck = new ArrayList<>();

        boolean canMoveRight = true;
        boolean canMoveLeft = true;
        boolean canMoveUp = true;
        boolean canMoveDown = true;

        for (int i = 1; i < 8; i++) {

            //Checks right horizontal movement
            if (canMoveRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor());

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color == this.color) {
                            movesForCheck.add(newMove.getToken());

                        }
                        canMoveRight = ifPieceKing(newMove);

                    } else {
                        movesForCheck.add(newMove.getToken());
                    }
                } else {
                    canMoveRight = false;
                }
            }

            //Checks left horizontal movement
            if (canMoveLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor());

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color == this.color) {
                            movesForCheck.add(newMove.getToken());
                        }
                        canMoveLeft = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(newMove.getToken());
                    }
                } else {
                    canMoveLeft = false;
                }
            }

            //Checks for up movement
            if (canMoveUp) {
                newMove.setCoordinates(this.getXCoor(), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color == this.color) {
                            movesForCheck.add(newMove.getToken());
                        }
                        canMoveUp = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(newMove.getToken());
                    }
                } else {
                    canMoveUp = false;
                }
            }

            //Checks for down movement
            if (canMoveDown) {
                newMove.setCoordinates(this.getXCoor(), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color == this.color) {
                            movesForCheck.add(newMove.getToken());
                        }
                        canMoveDown = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(newMove.getToken());
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
     * @return All hashed potential moves for calculating check
     */
    public ArrayList<Integer> diagonalForCheck() {
        CoorPair newMove = new CoorPair(-1, -1);
        ArrayList<Integer> movesForCheck = new ArrayList<>();

        boolean canMoveUpRight = true;
        boolean canMoveUpLeft = true;
        boolean canMoveDownLeft = true;
        boolean canMoveDownRight = true;

        for (int i = 1; i < 8; i++) {

            //Checks diagonal to the up -> right
            if (canMoveUpRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color == this.color) {
                            movesForCheck.add(newMove.getToken());
                        }
                        canMoveUpRight = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(newMove.getToken());
                    }
                } else {
                    canMoveUpRight = false;
                }
            }


            //Checks diagonal to the up -> left
            if (canMoveUpLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor() - (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color == this.color) {
                            movesForCheck.add(newMove.getToken());
                        }
                        canMoveUpLeft = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(newMove.getToken());
                    }
                } else {
                    canMoveUpLeft = false;
                }
            }


            //Checks diagonal to the down -> left
            if (canMoveDownLeft) {
                newMove.setCoordinates(this.getXCoor() - (i * 60), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color == this.color) {
                            movesForCheck.add(newMove.getToken());
                        }
                        canMoveDownLeft = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(newMove.getToken());
                    }
                } else {
                    canMoveDownLeft = false;
                }
            }


            //Checks diagonal to the down -> right
            if (canMoveDownRight) {
                newMove.setCoordinates(this.getXCoor() + (i * 60), this.getYCoor() + (i * 60));

                if (newMove.isInBounds()) {
                    if (Main.currentPieceLocations[newMove.getToken()] != null) {
                        if (Main.currentPieceLocations[newMove.getToken()].color == this.color) {
                            movesForCheck.add(newMove.getToken());
                        }
                        canMoveDownRight = ifPieceKing(newMove);
                    } else {
                        movesForCheck.add(newMove.getToken());
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
        return Main.currentPieceLocations[newMove.getToken()] instanceof King;
    }
}