import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

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

    //Sets up AnchorPane of piece images
    AnchorPane anchorPane = constructBoard();

    //Used for sound effects
    SoundControl soundControl = new SoundControl();

    //Used for checking the location and color of all pieces on board
    //Integer represents token of coordinate pair
    public static Piece[] currentPieceLocations = new Piece[64];

    //Represents all pieces currently on the board
    public static List<Piece> allPieces = new ArrayList<>();
    public static List<Piece> whitePieces = new ArrayList<>();
    public static List<Piece> blackPieces = new ArrayList<>();

    //All queens that can be used for pawn promotion
    public static LinkedList<Piece> extraPieces = new LinkedList<>();
    //Logs the current player to move
    Piece.Color playerToMove = Piece.Color.WHITE;
    //If game is in checkmate status
    Boolean checkmate = false;


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
            currentPieceLocations[blackPieces.get(i).getCoordinates().getToken()] =blackPieces.get(i);

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
     * Checks if a king is in check in the current board state
     *
     * @param color the color of the player
     * @return If king is in check
     */
    public static boolean isKingInCheck(Piece.Color color) {
        Integer kingCoordinates = null;

        //Grab this king's Coordinates
        if ( color == Piece.Color.WHITE ) {
            //Find king
            for ( Piece piece : whitePieces ) {
                if ( piece instanceof King ) {
                    kingCoordinates = piece.getCoordinates().getToken();
                }
            }
        } else {
            for ( Piece piece : blackPieces ) {
                if ( piece instanceof King ) {
                    kingCoordinates = piece.getCoordinates().getToken();
                }
            }
        }

        //We will always find each king
        assert kingCoordinates != null;

        //Check all the moves for the opponent
        for ( Piece piece : (color == Piece.Color.WHITE) ? blackPieces : whitePieces) {
            if (piece.pieceType == Piece.PieceType.PAWN || piece.pieceType == Piece.PieceType.KING) {
                //If piece is a pawn or king we need special logic
                for ( Integer legalMove : piece.movesForCheck() ) {
                    if (Objects.equals(legalMove, kingCoordinates)) return true;
                }
            } else {
                for ( Integer legalMove : piece.findPotentialMoves() ) {
                    if (Objects.equals(legalMove, kingCoordinates)) return true;
                }
            }
        }

        //Return false if no moves were found to put king in check
        return false;
    }

    //Logs a pieces old coordinates before being moved
    CoorPair oldCoors;
    //Logs the legal moves a piece
    ArrayList<Integer> potentialMoves = new ArrayList<>();

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

        /*
        Check if this move is potentially legal
         */

        //Check if it's this player's turn
        if (playerToMove != piece.color) return false;

        //Check if desired move is potentially legal
        for (Integer potential : potentialMoves) {
            if (piece.getCoordinates().getToken() == potential) {
                isLegalMove = true;
                break;
            }
        }

        if (isLegalMove) {
            /*
            Checks if this move puts player to move's king in check
            If this move puts their king in check, then it's illegal.

            Will NOT check if there's a piece in the desired coordinates.
            */

            //If there's not a piece at these coordinates we can inject our new coordinates in and be safe
            if (currentPieceLocations[piece.getCoordinates().getToken()] == null) {
                //Removes original coordinate of piece, so we can check it's new potential position
                currentPieceLocations[oldCoors.getToken()] = null;

                //Put new coordinates in
                currentPieceLocations[piece.getCoordinates().getToken()] = piece;

                //Not a legal move is king is now in check
                isLegalMove = !isKingInCheck(piece.color);

                //Remove new coordinates
                Main.currentPieceLocations[piece.getCoordinates().getToken()] = null;

                //Restore old coordinates
                Main.currentPieceLocations[oldCoors.getToken()] = piece;
            }
        }

        /*
        Check for en-passant if pawn
         */

        if (isLegalMove && piece instanceof Pawn) {
            if (piece.color == Piece.Color.WHITE && piece.getYCoor() == 120.0
                    || piece.color == Piece.Color.BLACK && piece.getYCoor() == 300.0) {

                CoorPair tempCoor = new CoorPair(piece.getCoordinates());
                Piece tempPiece;

                //Increment up/down space based on color
                tempCoor.setyCoor(tempCoor.getyCoor() + ((piece.color == Piece.Color.WHITE) ? 60.0 : -60.0));

                //Check for piece in this space
                if (currentPieceLocations[tempCoor.getToken()] != null) {
                    tempPiece = currentPieceLocations[tempCoor.getToken()];

                    //Check if pawn
                    if (tempPiece instanceof Pawn) {

                        //Only pawns of opposite color will be en-passantable here
                        if (((Pawn) tempPiece).enPassantable) {

                            //Remove captured piece
                            //This is safe because we will never be able to en-passant and capture
                            //when checking for checkmate
                            tempPiece.setCoordinates(-1000, -1000);
                            allPieces.remove(tempPiece);
                            currentPieceLocations[tempCoor.getToken()] = null;

                            return true;
                        }
                    }
                }
            }
        }

        return isLegalMove;
    }

    /**
     * Check if we are capturing a piece
     * If this move is legal and capturing a piece, remove the opponent piece from the board
     * @param piece Piece to move
     * @return If the piece captured an enemy piece or not
     */
    public boolean checkForCapture(Piece piece) {
        boolean isLegalMove = true;
        /*
        Checks if there's a piece in the space the player is trying to move to
        */

        if ( currentPieceLocations[piece.getCoordinates().getToken()] == null ) return true;

        //if there's a piece there of a different color
        if (currentPieceLocations[piece.getCoordinates().getToken()].color != piece.color) {

            //Save the piece we're attempting to capture
            Piece capturedPiece = currentPieceLocations[piece.getCoordinates().getToken()];
            currentPieceLocations[piece.getCoordinates().getToken()] = null;

            //Saves the "captured" pieces coordinates
            CoorPair capturedOldCoors = capturedPiece.getCoordinates();

            //Change coordinates of this piece to prevent move calculation for it
            capturedPiece.setCoordinates(-1000, -1000);

            //Temporarily change this pieces coordinates
            currentPieceLocations[oldCoors.getToken()] = null;
            currentPieceLocations[piece.getCoordinates().getToken()] = piece;

            //Check if king is in check
            isLegalMove = !isKingInCheck(piece.color);

            //Give pieces its coordinates back and restore pieces map
            capturedPiece.setCoordinates(capturedOldCoors.getxCoor(), capturedOldCoors.getyCoor());
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
     * Set user input actions for each piece
     */
    public void setActions() {
        //Set actions for all pieces
        for (Piece piece : allPieces) {
            piece.pieceObject.setOnMouseEntered(event -> piece.pieceObject.setCursor(Cursor.OPEN_HAND));

            piece.pieceObject.setOnMousePressed(event -> {

                //Logs old coordinates
                oldCoors = new CoorPair(piece.getXCoor(), piece.getYCoor());

                //Update cursor
                piece.pieceObject.setCursor(Cursor.CLOSED_HAND);
                piece.pieceObject.toFront();


                //Find all legal moves for chosen piece
                potentialMoves = piece.findPotentialMoves();
            });

            piece.pieceObject.setOnMouseDragged(event -> {

                piece.pieceObject.setCursor(Cursor.CLOSED_HAND);
                //Must adjust coordinates by 30 before "attaching" to mouse
                piece.setXCoor(event.getSceneX() - 30);
                piece.setYCoor(event.getSceneY() - 30);
                piece.draw();

            });

            piece.pieceObject.setOnMouseReleased(event -> {

                //Update cursor
                piece.pieceObject.setCursor(Cursor.OPEN_HAND);

                //For SFX control
                boolean playedSFX = false;
                boolean playCapture = false;
                boolean promoted = false;

                //Finds the nearest space to the cursor
                piece.findNearestSpace();

                //Used to save if this is a legal move
                boolean isLegalMove;

                //Save size for checking if we captured piece
                int size = allPieces.size();

                //Check if this move is legal
                isLegalMove = isPotentialMoveLegal(piece);

                //Check if we're making a legal capture
                if ( isLegalMove ) {
                    isLegalMove = checkForCapture(piece);
                }

                //Check if we captured a piece
                if ( size > allPieces.size() ) playCapture = true;


                if (!isLegalMove) {
                    //Bounces piece back to its original coordinates
                    piece.setCoordinates(oldCoors.getxCoor(), oldCoors.getyCoor());

                } else {
                    currentPieceLocations[oldCoors.getToken()] = null;

                    //Clear pawn en-passantable booleans
                    for ( Piece pawn : allPieces ) {
                        if ( pawn instanceof Pawn ) {
                            ((Pawn) pawn).enPassantable = false;
                        }
                    }

                    //Check if opponent king is now in check
                    if (isKingInCheck(getOpponentColor(piece.color))) {
                        soundControl.playCheck();
                        playedSFX = true;
                        playCapture = false;
                    }

                    //Check if we need to play the capture sound effect
                    if (playCapture) {
                        soundControl.playCapture();
                        playedSFX = true;
                    }

                    //Switch the turn
                    if (playerToMove == Piece.Color.WHITE) {
                        playerToMove = Piece.Color.BLACK;
                    } else {
                        playerToMove = Piece.Color.WHITE;
                    }

                    //If a pawn moves we need to set first move to false and check for piece promotion
                    if (piece instanceof Pawn) {
                        ((Pawn) piece).setFirstMoveFalse();

                        /*
                        Handle Pawn promotion logic
                         */
                        if (piece.getYCoor() == 0 || piece.getYCoor() == 420) {
                            //Set promotion flag
                            promoted = true;

                            //Gets a new queen in the correct color
                            Piece newQueen = (piece.color == Piece.Color.WHITE) ? extraPieces.getFirst() : extraPieces.getLast();

                            //Remove queen from extra piece list
                            extraPieces.remove(newQueen);

                            //Set Queen to new coordinates
                            newQueen.setCoordinates(piece.getCoordinates());
                            piece.setCoordinates(-1000, -1000);
                            currentPieceLocations[newQueen.getCoordinates().getToken()] = newQueen;

                            //Update relevant piece lists
                            allPieces.add(newQueen);
                            allPieces.remove(piece);
                            if ( newQueen.color == Piece.Color.WHITE) {
                                whitePieces.remove(piece);
                                whitePieces.add(newQueen);
                            } else {
                                blackPieces.remove(piece);
                                blackPieces.add(newQueen);
                            }
                        }

                    //Check if rook
                    } else if (piece instanceof Rook) {

                        ((Rook) piece).setFirstMoveFalse();

                    //Check if king
                    } else if (piece instanceof King) {
                        //Check if we're castling
                        //TODO: optimize
                        for (Piece rook : Main.allPieces) {
                            if (rook.pieceType == Piece.PieceType.ROOK & rook.color == piece.color) {
                                if (((Rook) rook).getFirstMoveStatus()) {
                                    //If Right rook can castle
                                    if (rook.getCoordinates()
                                            .coorEquals(new CoorPair(oldCoors.getxCoor() + 180, oldCoors.getyCoor()))) {
                                        //Checks if this move is trying to castle
                                        if (piece.getCoordinates()
                                                .coorEquals(
                                                        new CoorPair(oldCoors.getxCoor() + 120, oldCoors.getyCoor()))) {
                                            //Update location of rook
                                            currentPieceLocations[rook.getCoordinates().getToken()] = null;
                                            rook.setCoordinates(oldCoors.getxCoor() + 60, oldCoors.getyCoor());
                                            currentPieceLocations[rook.getCoordinates().getToken()] = rook;

                                            soundControl.playCastling();

                                            playedSFX = true;
                                            //If we've castled there's no need to continue checking pieces
                                            break;
                                        }
                                    }
                                    //If Left rook can castle
                                    if (rook.getCoordinates()
                                            .coorEquals(new CoorPair(oldCoors.getxCoor() - 240, oldCoors.getyCoor()))) {
                                        //Checks if this move is trying to castle
                                        if (piece.getCoordinates()
                                                .coorEquals(
                                                        new CoorPair(oldCoors.getxCoor() - 120, oldCoors.getyCoor()))) {
                                            //Update location of rooks
                                            currentPieceLocations[rook.getCoordinates().getToken()] = null;
                                            rook.setCoordinates(oldCoors.getxCoor() - 60, oldCoors.getyCoor());
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

                        ((King) piece).setFirstMoveFalse();
                    }


                    //If we haven't played a sound effect, we need to play the move sound effect
                    if (!playedSFX) {
                        soundControl.playMove();
                    }

                    if (!promoted) {
                        //Update pieces location
                        currentPieceLocations[piece.getCoordinates().getToken()] = piece;
                    }

                    //Draw new location to board
                    piece.draw();


                    /*
                    Check if the opponent king is now in checkmate
                     */

                    //First check if king is now in check
                    if ( isKingInCheck(getOpponentColor(piece.color)) ) {
                        //Temporarily sets checkmate to true
                        checkmate = true;
                        //Check if opponent can make any legal moves
                        for ( Piece pieceToCheck : (piece.color == Piece.Color.WHITE) ? blackPieces : whitePieces ) {
                            //Exit loop if mate
                            if ( !checkmate ) break;

                            //Find all potential moves
                            potentialMoves = pieceToCheck.findPotentialMoves();

                            oldCoors = pieceToCheck.getCoordinates();
                            for ( Integer move : potentialMoves) {
                                pieceToCheck.setCoordinates(move);
                                //Check move
                                if (isPotentialMoveLegal(pieceToCheck)) {
                                    //Found legal move means no checkmate
                                    checkmate = false;
                                    break;
                                }
                            }
                            pieceToCheck.setCoordinates(oldCoors.getxCoor(), oldCoors.getyCoor());
                        }


                        if ( checkmate ) System.out.println("Checkmate.");
                    }
                }
            });
        }
    }
}