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
    //Integer represents HashCode for coordinate pair
    public static HashMap<Integer, Piece> currentPieceLocations = new HashMap<>();

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
        constructPieces();
        setActions();

        //Makes AnchorPane for spaces
        AnchorPane spaces = new AnchorPane();
        spaces.setMaxSize(480, 480);

        for (Piece piece : allPieces) {
            spaces.getChildren().add(piece.pieceObject);
        }

        //Removes extra pieces from allPieces list
        for (int i = 0; i < allPieces.size(); i++) {
            if (allPieces.get(i).getCoordinates().coorEquals(new CoorPair(-1000, -1000))) {
                allPieces.remove(i);
                --i;
            }
        }

        return spaces;
    }

    /**
     * Creates all the pieces on the chessboard
     */
    public void constructPieces() throws FileNotFoundException {
        //Creates new pieces in order that aren't pawns in both lists
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
        for ( int whiteY = 7, blackY = 0, xCoor = 0;
                xCoor < 8; xCoor++ ) {

            blackPieces.get(xCoor).setCoordinates(xCoor * 60, blackY);
            currentPieceLocations.put(blackPieces.get(xCoor).getCoordinates().getHash(), blackPieces.get(xCoor));

            whitePieces.get(xCoor).setCoordinates(xCoor * 60, whiteY * 60);
            currentPieceLocations.put(whitePieces.get(xCoor).getCoordinates().getHash(), whitePieces.get(xCoor));

        }

        //Adds all pawns to end of lists
        for (int i = 0; i < 8; i++) {
            whitePieces.add(new Pawn(Piece.Color.WHITE));
            blackPieces.add(new Pawn(Piece.Color.BLACK));
        }

        //Set coordinates for each pawn
        for (int whiteY = 6, blackY = 1, xCoor = 0, i = 8; i < whitePieces.size(); i++) {
            blackPieces.get(i).setCoordinates(xCoor * 60, blackY * 60);
            whitePieces.get(i).setCoordinates(xCoor * 60, whiteY * 60);

            currentPieceLocations.put(whitePieces.get(i).getCoordinates().getHash(), whitePieces.get(i));
            currentPieceLocations.put(blackPieces.get(i).getCoordinates().getHash(), blackPieces.get(i));

            ++xCoor;
        }

        //Makes extra pieces
        for (int i = 0; i < 8; i++) {
            extraPieces.add(new Queen(Piece.Color.WHITE));
            extraPieces.add(new Queen(Piece.Color.BLACK));
        }

        allPieces.addAll(extraPieces);


        for (Piece extra : extraPieces) {
            //Sets coordinates off board for storage
            extra.setCoordinates(-1000, -1000);
        }

        //Puts all pieces into combined list
        allPieces.addAll(whitePieces);
        allPieces.addAll(blackPieces);


    }


    /**
     * Checks if a king is in check in the current board state
     *
     * @param color the color of the player
     * @return If king is in check
     */
    public static boolean isKingInCheck(Piece.Color color) {
        CoorPair kingCoordinates;
        /*
        Checks if we put the opponent in check
        */

        //Grabs the kings Coordinates
        if ( color == Piece.Color.WHITE ) {
            kingCoordinates = whitePieces.get(4).getCoordinates();
        } else {
            kingCoordinates = blackPieces.get(4).getCoordinates();
        }

        //Check all the moves for the opponent
        for ( Piece piece : (color == Piece.Color.WHITE) ? blackPieces : whitePieces) {
            if (piece.pieceType == Piece.PieceType.PAWN) {
                //If piece is a pawn we need special logic to only check diagonals
                for (CoorPair legalMove : piece.movesForCheck()) {
                    if (legalMove.getHash() == kingCoordinates.getHash()) return true;
                }
            } else {
                for (CoorPair legalMove : piece.findPotentialMoves()) {
                    if ( legalMove.getHash() == kingCoordinates.getHash() ) return true;
                }
            }
        }

        //Return false if no moves were found to put king in check
        return false;
    }

    //Logs a pieces old coordinates before being moved
    CoorPair oldCoors;
    //Logs the legal moves a piece
    ArrayList<CoorPair> potentialMoves = new ArrayList<>();

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
    public boolean isPotentialMoveLegal(Piece piece, boolean captureAllowed) {
        //Used to save if this is a legal move
        boolean isLegalMove = false;


        /*
        Check if this move is potentially legal
         */
        for (CoorPair potential : potentialMoves) {
            if ( playerToMove == piece.color && piece.getCoordinates().getHash() == potential.getHash()) {
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

            //Removes original coordinate of piece, so we can check it's new potential position
            currentPieceLocations.remove(oldCoors.getHash());

            //If there's not a piece at these coordinates we can inject our new coordinates in and be safe
            if (!currentPieceLocations.containsKey(piece.getCoordinates().getHash())) {
                //Put new coordinates in
                currentPieceLocations.put(piece.getCoordinates().getHash(), piece);

                //Not a legal move is king is now in check
                isLegalMove = !isKingInCheck(piece.color);

                //Remove new coordinates
                Main.currentPieceLocations.remove(piece.getCoordinates().getHash());
            }

            //Restores old coors
            Main.currentPieceLocations.put(oldCoors.getHash(), piece);
        }



        /*
        Checks if there's a piece in the space the player is trying to move to
        */
        if (currentPieceLocations.containsKey(piece.getCoordinates().getHash()) &
                isLegalMove) {
            //if there's a piece there of a different color
            if (currentPieceLocations.get(piece.getCoordinates().getHash()).color != piece.color) {
                //Save the piece we're capturing
                Piece capturedPiece = currentPieceLocations.get(piece.getCoordinates().getHash());

                //Saves the "captured" pieces coordinates
                CoorPair capturedOldCoors = capturedPiece.getCoordinates();

                //Change coordinates of this piece to prevent move calculation for it
                capturedPiece.setCoordinates(-1000, -1000);

                //Temporarily change this pieces coordinates
                currentPieceLocations.remove(oldCoors.getHash());
                currentPieceLocations.put(piece.getCoordinates().getHash(), piece);

                //Check if king is still in check
                isLegalMove = !isKingInCheck(piece.color);

                //Give pieces its coordinates back and restore pieces map
                capturedPiece.setCoordinates(capturedOldCoors.getxCoor(), capturedOldCoors.getyCoor());
                currentPieceLocations.put(oldCoors.getHash(), piece);
                currentPieceLocations.put(capturedOldCoors.getHash(), capturedPiece);

                //If this is a legal move we're capturing a piece
                //Must only go into this statement if we're allowed to capture pieces
                if (isLegalMove && captureAllowed) {
                    //Remove piece from board
                    allPieces.remove(capturedPiece);
                    currentPieceLocations.remove(capturedPiece.getCoordinates().getHash());

                    //Set coordinates off board
                    capturedPiece.setCoordinates(-1000, -1000);
                }
            }
        }

        /*
        Check for en-passant if pawn
         */

        if (isLegalMove && piece instanceof Pawn) {
            if (piece.color == Piece.Color.WHITE && piece.getYCoor() == 120.0
                    || piece.color == Piece.Color.BLACK && piece.getYCoor() == 300.0) {
                //Save potential captured piece
                CoorPair tempCoor = new CoorPair(piece.getCoordinates());
                Piece tempPiece;

                //Increment up/down space based on color
                tempCoor.setyCoor(tempCoor.getyCoor() + ((piece.color == Piece.Color.WHITE) ? 60.0 : -60.0));

                //Check for piece in this space

                if (currentPieceLocations.containsKey(tempCoor.getHash())) {
                    tempPiece = currentPieceLocations.get(tempCoor.getHash());

                    //Check if pawn
                    if (tempPiece instanceof Pawn) {

                        //Only pawns of opposite color will be en-passantable here
                        if (((Pawn) tempPiece).enPassantable) {

                            //Remove captured piece
                            tempPiece.setCoordinates(-1000, -1000);
                            allPieces.remove(tempPiece);
                            currentPieceLocations.remove(tempCoor.getHash());

                            return true;
                        }
                    }
                }
            }
        }

        return isLegalMove;
    }

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

                piece.pieceObject.setCursor(Cursor.OPEN_HAND);

                //For SFX control
                boolean playedSFX = false;
                boolean playCapture = false;
                //Finds the nearest space to the cursor
                piece.findNearestSpace();
                //Used to save if this is a legal move
                boolean isLegalMove;

                int size = allPieces.size();
                isLegalMove = isPotentialMoveLegal(piece, true);
                //Check if we captured a piece
                if ( size > allPieces.size() ) playCapture = true;


                if (!isLegalMove) {
                    //Bounces piece back to its original coordinates if it's not a legal move
                    piece.setCoordinates(oldCoors.getxCoor(), oldCoors.getyCoor());

                } else {
                    currentPieceLocations.remove(oldCoors.getHash());

                    //Clear pawn en-passantable booleans
                    for ( Piece pawn : allPieces ) {
                        if ( pawn instanceof Pawn ) {
                            ((Pawn) pawn).enPassantable = false;
                        }
                    }

                    if (isKingInCheck(getOpponentColor(piece.color))) {
                        soundControl.playCheck();
                        playedSFX = true;
                        playCapture = false;
                    }

                    //If we need to play the capture sound effect
                    if (playCapture) {
                        soundControl.playCapture();
                        playedSFX = true;
                    }

                    //Switches the turn
                    if (playerToMove == Piece.Color.WHITE) {
                        playerToMove = Piece.Color.BLACK;
                    } else {
                        playerToMove = Piece.Color.WHITE;
                    }

                    //If a pawn, rook, or king moves, we need to set first move to false
                    if (piece instanceof Pawn) {
                        ((Pawn) piece).setFirstMoveFalse();

                        /*
                        Handle Pawn promotion logic
                         */
                        if (piece.getYCoor() == 0 || piece.getYCoor() == 420) {
                            //Gets a new queen in the correct color
                            Piece newQueen = (piece.color == Piece.Color.WHITE) ? extraPieces.getFirst() : extraPieces.getLast();
                            //Remove queen from extra piece list
                            extraPieces.remove(newQueen);
                            //Set Queen to correct coordinates
                            newQueen.setCoordinates(piece.getXCoor(), piece.getYCoor());
                            piece.setCoordinates(-1000, -1000);
                            currentPieceLocations.put(newQueen.getCoordinates().getHash(), newQueen);
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

                    } else if (piece instanceof Rook) {

                        ((Rook) piece).setFirstMoveFalse();

                    } else if (piece instanceof King) {
                        //If we're castling
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
                                            currentPieceLocations.remove(rook.getCoordinates().getHash());
                                            rook.setCoordinates(oldCoors.getxCoor() + 60, oldCoors.getyCoor());
                                            currentPieceLocations.put(rook.getCoordinates().getHash(), rook);
                                            soundControl.playCastling();
                                            playedSFX = true;
                                        }
                                    }
                                    //If Left rook can castle
                                    if (rook.getCoordinates()
                                            .coorEquals(new CoorPair(oldCoors.getxCoor() - 240, oldCoors.getyCoor()))) {
                                        //Checks if this move is trying to castle
                                        if (piece.getCoordinates()
                                                .coorEquals(
                                                        new CoorPair(oldCoors.getxCoor() - 120, oldCoors.getyCoor()))) {
                                            currentPieceLocations.remove(rook.getCoordinates().getHash());
                                            rook.setCoordinates(oldCoors.getxCoor() - 60, oldCoors.getyCoor());
                                            currentPieceLocations.put(rook.getCoordinates().getHash(), rook);
                                            soundControl.playCastling();
                                            playedSFX = true;
                                        }
                                    }
                                }
                            }

                            ((King) piece).setFirstMoveFalse();
                        }
                    }


                    //If we haven't played a sound effect, we need to play the move sound effect
                    if (!playedSFX) {
                        soundControl.playMove();
                    }

                    currentPieceLocations.put(piece.getCoordinates().getHash(), piece);

                    piece.draw();


                    /*
                    Check if the opponent king is now in checkmate
                     */

                    //First check if king is now in check
                    if ( isKingInCheck(getOpponentColor(piece.color)) ) {
                        //Temporarily sets checkmate to true
                        checkmate = true;
                        //Check if opponent can make any legal moves
                        for ( Piece piece1 : (piece.color == Piece.Color.WHITE) ? blackPieces : whitePieces ) {
                            if ( !checkmate ) break;
                            potentialMoves = piece1.findPotentialMoves();
                            oldCoors = piece1.getCoordinates();
                            for ( CoorPair move : potentialMoves) {
                                piece1.setCoordinates(move.getxCoor(), move.getyCoor());
                                //Check move
                                if (isPotentialMoveLegal(piece1, false)) {
                                    //Found legal move means no checkmate
                                    checkmate = false;
                                    break;
                                }
                            }
                            piece1.setCoordinates(oldCoors.getxCoor(), oldCoors.getyCoor());
                        }


                        if ( checkmate ) System.out.println("Checkmate.");
                    }
                }
            });
        }
    }
}