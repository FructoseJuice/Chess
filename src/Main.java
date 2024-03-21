import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

import Utils.CoorPair;
import Utils.SoundControl;
import Utils.BitBoard;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    public Main() throws FileNotFoundException, MalformedURLException {}

    /*
    GLOBAL VARIABLES
     */

    //AnchorPane of piece images
    AnchorPane anchorPane = constructBoard();

    //Used for sound effects
    SoundControl soundControl = new SoundControl();

    //Used for checking the location and color of all pieces on board
    //Integer represents token of coordinate pair
    //Start squares from index 1
    public static Piece[] currentPieceLocations = new Piece[64];

    //Holds all pieces currently on the board
    public static List<Piece> allPieces = new ArrayList<>();
    public static List<Piece> whitePieces = new ArrayList<>();
    public static List<Piece> blackPieces = new ArrayList<>();

    //All queens that can be used for pawn promotion
    public static LinkedList<Piece> extraPieces = new LinkedList<>();
    //Saves a pieces old coordinates before being moved
    CoorPair oldCoors;
    //Saves the legal moves of a piece
    long potentialMovesBitBoard = 0L;
    //Logs the current player to move
    Piece.Color playerToMove = Piece.Color.WHITE;
    //If game is in checkmate status
    Boolean inCheckmate = false;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        //Sets up the background chess board
        Image board = new Image(new FileInputStream("images\\Chess_Board.png"));
        ImageView boardView = new ImageView(board);
        boardView.setFitHeight(480);

        //Adds background and pieces to board
        Group group = new Group();
        group.getChildren().add(boardView);
        group.getChildren().add(anchorPane);
        Scene scene = new Scene(group);


        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();



        //For SFX control
        try {
            soundControl.playGameStart();
        } catch (MalformedURLException ignored) {
        }
    }


    /**
     * Adds all pieces to anchorPane
     * @return AnchorPane with pieces
     */
    public AnchorPane constructBoard() throws FileNotFoundException {
        //Make pieces and set actions on every piece
        constructPieces();
        setActions();

        //Remove extra pieces from list
        allPieces.removeAll(extraPieces);

        //Makes AnchorPane for spaces
        AnchorPane spaces = new AnchorPane();
        spaces.setMaxSize(480, 480);

        //Add every piece to spaces
        for ( Piece piece : allPieces ) {
            spaces.getChildren().add(piece.pieceObject);
        }
        for ( Piece piece : extraPieces) {
            spaces.getChildren().add(piece.pieceObject);
        }


        return spaces;
    }


    /**
     * Creates all the pieces on the chessboard
     */
    public void constructPieces() throws FileNotFoundException {
        //Create new pieces that aren't pawns in order for both lists
        whitePieces.add(new Rook(Piece.Color.WHITE));
        whitePieces.add(new Knight(Piece.Color.WHITE));
        whitePieces.add(new Bishop(Piece.Color.WHITE));
        whitePieces.add(new Queen(Piece.Color.WHITE));
        whitePieces.add(new King(Piece.Color.WHITE));
        whitePieces.add(new Bishop(Piece.Color.WHITE));
        whitePieces.add(new Knight(Piece.Color.WHITE));
        whitePieces.add(new Rook(Piece.Color.WHITE));


        blackPieces.add(new Rook(Piece.Color.BLACK));
        blackPieces.add(new Knight(Piece.Color.BLACK));
        blackPieces.add(new Bishop(Piece.Color.BLACK));
        blackPieces.add(new Queen(Piece.Color.BLACK));
        blackPieces.add(new King(Piece.Color.BLACK));
        blackPieces.add(new Bishop(Piece.Color.BLACK));
        blackPieces.add(new Knight(Piece.Color.BLACK));
        blackPieces.add(new Rook(Piece.Color.BLACK));


        //Set coordinates for each piece that's not a pawn
        for ( int xCoor = 0; xCoor < 8; xCoor++ ) {

            blackPieces.get(xCoor).setCoordinates(xCoor * 60, 0);
            currentPieceLocations[blackPieces.get(xCoor).getCoordinates().getToken()] = blackPieces.get(xCoor);

            whitePieces.get(xCoor).setCoordinates(xCoor * 60, 420);
            currentPieceLocations[whitePieces.get(xCoor).getCoordinates().getToken()] = whitePieces.get(xCoor);

        }

        //Adds all pawns to end of lists
        for (int i = 0; i < 8; i++) {
            whitePieces.add(new Pawn(Piece.Color.WHITE));
            blackPieces.add(new Pawn(Piece.Color.BLACK));
        }

        //Set coordinates for each pawn
        for (int xCoor = 0, i = 8; i < whitePieces.size(); i++) {
            blackPieces.get(i).setCoordinates(xCoor * 60, 60);
            whitePieces.get(i).setCoordinates(xCoor * 60, 360);

            currentPieceLocations[whitePieces.get(i).getCoordinates().getToken()] = whitePieces.get(i);
            currentPieceLocations[blackPieces.get(i).getCoordinates().getToken()] = blackPieces.get(i);

            ++xCoor;
        }

        //Makes extra pieces
        for (int i = 0; i < 8; i++) {
            extraPieces.add(new Queen(Piece.Color.WHITE));
            extraPieces.getLast().setCoordinates(-1000, -1000);

            extraPieces.add(new Queen(Piece.Color.BLACK));
            extraPieces.getLast().setCoordinates(-1000, -1000);
        }


        //Puts all pieces into combined list
        allPieces.addAll(whitePieces);
        allPieces.addAll(blackPieces);
        allPieces.addAll(extraPieces);
    }


    /**
     * Takes a players color and returns the opponents color.
     * @param color color of the player in question
     * @return oponents color
     */
    public Piece.Color getOpponentColor(Piece.Color color) {
        return (color == Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;
    }


    /**
     * Takes a pieces coordinates, checks if this move
     * puts the players king into check, or takes the players king out of check.
     * Also checks for en-passant.
     *
     * @param piece piece that is being moved
     * @return if the move being made is legal
     */
    public boolean isPotentialMoveLegal(Piece piece) {
        boolean isLegalMove = false;

        int moveToken = piece.getCoordinates().getToken();

        /*
        Check if this move is potentially legal
         */

        //Check if it's this player's turn
        if (playerToMove != piece.color) return false;

        //Check if desired move is potentially legal
        if ( BitBoard.compareToken(moveToken, potentialMovesBitBoard) ) {
            isLegalMove = true;
        }

        if (isLegalMove) {
            /*
            Checks if this move puts player to move's king in check
            If this move puts their king in check, then it's illegal.

            Will NOT check if there's a piece in the desired coordinates.
            */

            //If there's not a piece at these coordinates we can inject our new coordinates in and be safe
            if (currentPieceLocations[moveToken] == null) {
                //Removes original coordinate of piece, so we can check it's new potential position
                currentPieceLocations[oldCoors.getToken()] = null;

                //Put new coordinates in
                currentPieceLocations[moveToken] = piece;

                //Not a legal move is king is now in check
                isLegalMove = !isKingInCheck(piece.color);

                //Remove new coordinates
                Main.currentPieceLocations[moveToken] = null;

                //Restore old coordinates
                Main.currentPieceLocations[oldCoors.getToken()] = piece;
            }
        }

        /*
        Check for en-passant if pawn
         */

        if (isLegalMove && piece instanceof Pawn) {
            //Check if on correct row
            if (piece.color == Piece.Color.WHITE && piece.getYCoor() == 120.0
                    || piece.color == Piece.Color.BLACK && piece.getYCoor() == 300.0) {
                if (checkEnPassant(piece)) return true;
            }
        }

        return isLegalMove;
    }

    /**
     * Check if the player is making a legal en-passant.
     *
     * @param piece Moved piece
     * @return If en passant legal
     */
    public boolean checkEnPassant(Piece piece) {
        CoorPair enemyCoor = new CoorPair(piece.getCoordinates());

        //Increment up/down space based on color
        enemyCoor.setYCoor(enemyCoor.getYCoor() + ((piece.color == Piece.Color.WHITE) ? 60.0 : -60.0));

        //Check for piece in this space
        if (currentPieceLocations[enemyCoor.getToken()] != null) {
            Piece enemy;

            enemy = currentPieceLocations[enemyCoor.getToken()];

            //Ensure enemy piece is a pawn
            if (!(enemy instanceof Pawn)) return false;

            //Only pawns of opposite color will be en-passantable here
            if (((Pawn) enemy).enPassantable) {
                //Remove captured piece
                //This is safe because we will never be able to en-passant and capture
                //when checking for checkmate
                enemy.setCoordinates(-1000, -1000);
                allPieces.remove(enemy);
                currentPieceLocations[enemyCoor.getToken()] = null;

                return true;
            }
        }

        return false;
    }


    /**
     * Check if we are capturing a piece
     * If this move is legal and capturing a piece, remove the opponent piece from the board
     * @param piece Piece to move
     * @return If the piece captured an enemy piece or not
     */
    public boolean handleCapture(Piece piece) {
        boolean isLegalMove = true;

        int move = piece.getCoordinates().getToken();

        /*
        Checks if there's a piece in the space the player is trying to move to
        */

        if ( currentPieceLocations[move] == null ) return true; //True for legal move

        //if there's a piece there of a different color
        if (currentPieceLocations[move].color != piece.color) {

            //Save the piece we're attempting to capture
            Piece capturedPiece = currentPieceLocations[move];
            currentPieceLocations[move] = null;

            //Saves the "captured" pieces coordinates
            CoorPair capturedOldCoors = capturedPiece.getCoordinates();

            //Change coordinates of this piece to prevent move calculation for it
            capturedPiece.setCoordinates(-1000, -1000);

            //Temporarily change this pieces coordinates
            currentPieceLocations[oldCoors.getToken()] = null;
            currentPieceLocations[move] = piece;

            //Check if king is in check
            isLegalMove = !isKingInCheck(piece.color);

            //Give pieces its coordinates back and restore pieces map
            capturedPiece.setCoordinates(capturedOldCoors.getXCoor(), capturedOldCoors.getYCoor());
            currentPieceLocations[oldCoors.getToken()] = piece;
            currentPieceLocations[capturedOldCoors.getToken()] = capturedPiece;

            //If this is a legal move we're capturing a piece
            if (isLegalMove) {
                //Remove piece from board
                allPieces.remove(capturedPiece);
                currentPieceLocations[capturedPiece.getCoordinates().getToken()] = null;

                //Set coordinates off board
                capturedPiece.setCoordinates(-1000, -1000);
            }
        }

        return isLegalMove;
    }


    /**
     * Checks if @pawn can promote, if it can promote
     * handle logic to replace it with a queen of the correct color.
     * @param pawn Pawn to check
     * @return If @pawn has promoted or not
     */
    public boolean handlePawnPromotion(Piece pawn) {
        if (pawn.getYCoor() == 0 || pawn.getYCoor() == 420) {
            //Gets a new queen in the correct color
            Piece newQueen = (pawn.color == Piece.Color.WHITE) ? extraPieces.getFirst() : extraPieces.getLast();

            //Remove queen from extra piece list
            extraPieces.remove(newQueen);

            //Set Queen to new coordinates
            newQueen.setCoordinates(pawn.getCoordinates());
            pawn.setCoordinates(-1000, -1000);
            currentPieceLocations[newQueen.getCoordinates().getToken()] = newQueen;

            //Update relevant piece lists
            allPieces.add(newQueen);
            allPieces.remove(pawn);
            if ( newQueen.color == Piece.Color.WHITE) {
                whitePieces.remove(pawn);
                whitePieces.add(newQueen);
            } else {
                blackPieces.remove(pawn);
                blackPieces.add(newQueen);
            }

            return true;
        }

        return false;
    }


    /**
     * Checks if a king is in check in the current board state
     *
     * @param color the color of the player
     * @return If king is in check
     */
    public static boolean isKingInCheck(Piece.Color color) {
        int kingToken = -1;

        //Grab this king's Coordinates
        for( Piece piece : (color == Piece.Color.WHITE) ? whitePieces : blackPieces) {
            if ( piece instanceof King ) {
                kingToken = piece.getCoordinates().getToken();
            }
        }

        //Check all the moves for the opponent
        for ( Piece piece : (color == Piece.Color.WHITE) ? blackPieces : whitePieces) {
            if (piece.pieceType == Piece.PieceType.PAWN || piece.pieceType == Piece.PieceType.KING) {
                //If piece is a pawn or king we need special logic
                if ( BitBoard.compareToken(kingToken, piece.movesForCheck()) ) {
                    return true;
                }

            } else {
                if ( BitBoard.compareToken(kingToken, piece.findPotentialMoves()) ) {
                    return true;
                }

            }
        }

        //Return false if no moves were found to put king in check
        return false;
    }


    /**
     * Checks given color to see if that player can make any moves
     * @param colorToCheck Color to check
     */
    public void inCheckmate(Piece.Color colorToCheck) {
        //Ensure king is in check
        if(!isKingInCheck(getOpponentColor(colorToCheck))) return;

        //Temporarily sets checkmate to true
        inCheckmate = true;

        //Check if opponent can make any legal moves
        for (Piece pieceToCheck : (colorToCheck == Piece.Color.WHITE) ? blackPieces : whitePieces) {
            //Find all potential moves
            potentialMovesBitBoard = pieceToCheck.findPotentialMoves();

            oldCoors = pieceToCheck.getCoordinates();

            for ( int i = 0; i < 64; i++ ) {
                //Check all potential moves
                if ( BitBoard.compareToken(i, potentialMovesBitBoard) ) {
                    pieceToCheck.setCoordinates(i);

                    if ( isPotentialMoveLegal(pieceToCheck)) {
                        inCheckmate = false;
                        break;
                    }
                }
            }

            //Restore coordinates of piece we're checking
            pieceToCheck.setCoordinates(oldCoors);

            //Exit loop if not mate
            if (!inCheckmate) break;
        }

        if (inCheckmate) System.out.println("Checkmate.");
    }

    /**
     * Set user input actions for each piece
     */
    public void setActions() {
        //Set actions for all pieces
        for (Piece movedPiece : allPieces) {
            movedPiece.pieceObject.setOnMouseEntered(event -> movedPiece.pieceObject.setCursor(Cursor.OPEN_HAND));

            movedPiece.pieceObject.setOnMousePressed(event -> {

                //Logs old coordinates
                oldCoors = new CoorPair(movedPiece.getXCoor(), movedPiece.getYCoor());

                //Update cursor
                movedPiece.pieceObject.setCursor(Cursor.CLOSED_HAND);
                movedPiece.pieceObject.toFront();

                //Find all potentially legal moves for chosen piece
                potentialMovesBitBoard = movedPiece.findPotentialMoves();
            });

            movedPiece.pieceObject.setOnMouseDragged(event -> {

                //Update cursor
                movedPiece.pieceObject.setCursor(Cursor.CLOSED_HAND);

                //Center the piece on mouse throughout mouse movement
                movedPiece.setXCoor(event.getSceneX() - 30);
                movedPiece.setYCoor(event.getSceneY() - 30);
                movedPiece.draw();

            });

            movedPiece.pieceObject.setOnMouseReleased(event -> {

                //Update cursor
                movedPiece.pieceObject.setCursor(Cursor.OPEN_HAND);

                boolean playedSFX = false;
                boolean playCapture = false;
                boolean promoted = false;
                boolean isLegalMove = false;

                //BitBoard.printBitboard(potentialMovesBitBoard);

                //Find the nearest space to the cursor
                movedPiece.findNearestSpace();

                //Save size for checking if we captured piece
                int size = allPieces.size();

                //Check if this move is legal
                if (!inCheckmate) {
                    isLegalMove = isPotentialMoveLegal(movedPiece);
                }

                //Check if we're making a legal capture
                if ( isLegalMove ) {
                    isLegalMove = handleCapture(movedPiece);
                }

                //Check if we captured a piece
                if ( size > allPieces.size() ) playCapture = true;


                if (!isLegalMove) {
                    //Bounces piece back to its original coordinates
                    movedPiece.setCoordinates(oldCoors.getXCoor(), oldCoors.getYCoor());

                } else {
                    //Remove piece from old coordinates
                    currentPieceLocations[oldCoors.getToken()] = null;

                    //Switch the turn
                    if (playerToMove == Piece.Color.WHITE) {
                        playerToMove = Piece.Color.BLACK;
                    } else {
                        playerToMove = Piece.Color.WHITE;
                    }

                    //Clear pawn en-passantable booleans
                    for ( Piece pawn : allPieces ) {
                        if ( pawn instanceof Pawn ) {
                            ((Pawn) pawn).enPassantable = false;
                        }
                    }

                    //Check if opponent king is now in check
                    if (isKingInCheck(getOpponentColor(movedPiece.color))) {
                        soundControl.playCheck();
                        playedSFX = true;
                        playCapture = false;
                    }

                    //Check if we need to play the capture sound effect
                    if (playCapture) {
                        soundControl.playCapture();
                        playedSFX = true;
                    }

                    //If a pawn moves we need to set first move to false and check for piece promotion
                    if (movedPiece instanceof Pawn) {
                        ((Pawn) movedPiece).setFirstMoveFalse();
                        //Checks for and handles pawn promotion
                        promoted = handlePawnPromotion(movedPiece);
                    }

                    //If a rook, set first move to false
                    if (movedPiece instanceof Rook) {
                        ((Rook) movedPiece).setFirstMoveFalse();
                    }

                    //Check if king
                    if (movedPiece instanceof King) {
                        ((King) movedPiece).setFirstMoveFalse();
                        //Check if we're castling
                        for (Piece rook : (movedPiece.color == Piece.Color.WHITE) ? whitePieces : blackPieces) {
                            if (rook.pieceType == Piece.PieceType.ROOK) {
                                //Checking right rook
                                if (rook.getCoordinates()
                                        .coorEquals(new CoorPair(oldCoors.getXCoor() + 180, oldCoors.getYCoor()))) {
                                    //Checks if this move is trying to castle with right rook
                                    if (movedPiece.getCoordinates()
                                            .coorEquals(
                                                    new CoorPair(oldCoors.getXCoor() + 120, oldCoors.getYCoor()))) {
                                        //Update location of rook
                                        currentPieceLocations[rook.getCoordinates().getToken()] = null;
                                        rook.setCoordinates(rook.getXCoor() - 120, oldCoors.getYCoor());
                                        currentPieceLocations[rook.getCoordinates().getToken()] = rook;

                                        soundControl.playCastling();

                                        playedSFX = true;

                                        //If we've castled there's no need to continue checking pieces
                                        break;
                                    }
                                }
                                //Checking left rook
                                if (rook.getCoordinates()
                                        .coorEquals(new CoorPair(oldCoors.getXCoor() - 240, oldCoors.getYCoor()))) {
                                    //Checks if this move is trying to castle with left rook
                                    if (movedPiece.getCoordinates()
                                            .coorEquals(
                                                    new CoorPair(oldCoors.getXCoor() - 120, oldCoors.getYCoor()))) {
                                        //Update location of rook
                                        currentPieceLocations[rook.getCoordinates().getToken()] = null;
                                        rook.setCoordinates(rook.getXCoor() + 180, rook.getYCoor());
                                        currentPieceLocations[rook.getCoordinates().getToken()] = rook;

                                        soundControl.playCastling();

                                        playedSFX = true;

                                        //If we've castled there's no need to continue checking pieces
                                        break;
                                    }
                                }
                            }
                        }
                    }


                    //If we haven't played a sound effect, we need to play the move sound effect
                    if (!playedSFX) {
                        soundControl.playMove();
                    }

                    //If we haven't promoted, update piece locations
                    if (!promoted) {
                        currentPieceLocations[movedPiece.getCoordinates().getToken()] = movedPiece;
                    }

                    //Draw new location to board
                    movedPiece.draw();

                    //Check if player has put the opponent in checkmate
                    inCheckmate(movedPiece.color);
                }
            });
        }
    }
}