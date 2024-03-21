import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import Utils.CoorPair;
import Utils.SoundControl;
import Utils.BitBoard;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Main extends Application {
    public Main() throws MalformedURLException {}

    /*
    GLOBAL VARIABLES
     */
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
        group.getChildren().add(Board.anchorPane);
        Scene scene = new Scene(group);

        //Set action events on pieces
        for (Piece piece : Board.pieces.getAllPieces()) {
            setActions(piece);
        }

        //Set properties of main stage
        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        //For SFX control
        try {
            SoundControl.playGameStart();
        } catch (MalformedURLException ignored) {
        }
    }

    /**
     * Set user input actions for each piece
     */
    public void setActions(Piece piece) {
        piece.pieceObject.setOnMouseEntered(event -> piece.pieceObject.setCursor(Cursor.OPEN_HAND));

        piece.pieceObject.setOnMousePressed(event -> {
            //Update cursor
            piece.pieceObject.setCursor(Cursor.CLOSED_HAND);
            piece.pieceObject.toFront();

            //Logs old coordinates
            oldCoors = new CoorPair(piece.getXCoor(), piece.getYCoor());

            //Find all potentially legal moves for chosen piece
            potentialMovesBitBoard = piece.findPotentialMoves();
        });

        piece.pieceObject.setOnMouseDragged(event -> {
            //Update cursor
            piece.pieceObject.setCursor(Cursor.CLOSED_HAND);

            //Don't drag piece if checkmate
            if (inCheckmate) {
                //Reset cursor
                piece.pieceObject.setCursor(Cursor.OPEN_HAND);
                return;
            }


            //Center the piece on mouse throughout mouse movement
            piece.setXCoor(event.getSceneX() - 30);
            piece.setYCoor(event.getSceneY() - 30);
            piece.draw();

        });

        piece.pieceObject.setOnMouseReleased(event -> {
            //Update cursor
            piece.pieceObject.setCursor(Cursor.OPEN_HAND);

            //Handle logic for moving piece
            handlePieceMovement(piece);
        });
    }

    /**
     * Handles all the main logic for moving a piece.
     * @param movedPiece Piece moved by player
     */
    public void handlePieceMovement(Piece movedPiece) {
        boolean playedSFX = false;
        boolean playCapture = false;
        boolean promoted = false;
        boolean isLegalMove;

        //BitBoard.printBitboard(potentialMovesBitBoard);

        //Find the nearest space to the cursor
        movedPiece.findNearestSpace();

        //Check if this move is legal
        isLegalMove = isPotentialMoveLegal(movedPiece);

        //Check if we're making a legal capture
        if (isLegalMove) {
            if (isMoveForCapture(movedPiece)) {
                isLegalMove = handleCapture(movedPiece);

                if (isLegalMove) playCapture = true;
            }
        }

        if (!isLegalMove) {
            //Bounces piece back to its original coordinates and short circuits logic
            movedPiece.setCoordinates(oldCoors.getXCoor(), oldCoors.getYCoor());
            return;
        }

        //Switch the turn since this is a legal move
        if (playerToMove == Piece.Color.WHITE) {
            playerToMove = Piece.Color.BLACK;
        } else {
            playerToMove = Piece.Color.WHITE;
        }

        //Remove piece from old coordinates
        Board.currentPieceLocations[oldCoors.getToken()] = null;

        //If a pawn moves we need to set first move to false and check for piece promotion
        if (movedPiece instanceof Pawn) {
            ((Pawn) movedPiece).setFirstMoveFalse();

            //Checks for and handles pawn promotion
            try {
                promoted = (handlePawnPromotion(movedPiece));
            } catch (FileNotFoundException e) {
                //Occurs when new piece doesn't have an image on file
                throw new RuntimeException(e);
            }
        }

        if (!promoted) {
            //If we haven't promoted, update piece locations with new move
            Board.currentPieceLocations[movedPiece.getCoordinates().getToken()] = movedPiece;
        }

        //Check if opponent king is now in check
        if (isKingInCheck(getOpponentColor(movedPiece.color))) {
            SoundControl.playCheck();

            playedSFX = true;
            playCapture = false;
        }

        //If a rook, set first move to false
        if (movedPiece instanceof Rook) {
            ((Rook) movedPiece).setFirstMoveFalse();
        }

        //Check if king
        if (movedPiece instanceof King) {
            //Check for and handle castle
            if (((King) movedPiece).isFirstMove()) {
                handleCastle(movedPiece);
            }

            //Set first move false
            ((King) movedPiece).setFirstMoveFalse();
        }

        //Clear pawn en-passantable booleans
        for (Piece pawn : Board.pieces.getAllPieces()) {
            if (pawn instanceof Pawn) {
                ((Pawn) pawn).enPassantable = false;
            }
        }

        //Check if we need to play the capture sound effect
        if (playCapture) {
            SoundControl.playCapture();

            playedSFX = true;
        }

        //If we haven't played a sound effect, we need to play the move sound effect
        if (!playedSFX) {
            SoundControl.playMove();
        }

        //Draw new location to board
        movedPiece.draw();

        //Check if player has put the opponent in checkmate
        inCheckmate(movedPiece.color);
    }

    /**
     * Takes a pieces coordinates, checks if this move
     * puts the players king into check, or takes the players king out of check.
     * Also checks for en-passant.
     *
     * @param piece piece that is being moved
     * @return if the move being made is legal
     */
    private boolean isPotentialMoveLegal(Piece piece) {
        boolean isLegalMove = true;

        int moveToken = piece.getCoordinates().getToken();

        /*
        Check if this move is potentially legal
         */

        //Check if it's this player's turn
        if (playerToMove != piece.color) return false;

        //Check if desired move is potentially legal
        if (!BitBoard.compareToken(moveToken, potentialMovesBitBoard)) return false;

        /*
        Checks if this move puts player to move's king in check
        if this move puts their king in check, then it's illegal.

        Will NOT check if there's a piece in the desired coordinates.
         */

        //If there's not a piece at these coordinates we can inject our new coordinates in and be safe
        if (Board.currentPieceLocations[moveToken] == null) {
            //Removes original coordinate of piece, so we can check it's new potential position
            Board.currentPieceLocations[oldCoors.getToken()] = null;

            //Put new coordinates in
            Board.currentPieceLocations[moveToken] = piece;

            //Not a legal move is king is now in check
            isLegalMove = !isKingInCheck(piece.color);

            //Remove new coordinates
            Board.currentPieceLocations[moveToken] = null;

            //Restore old coordinates
            Board.currentPieceLocations[oldCoors.getToken()] = piece;
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
     * Checks if the player is attempting to capture a piece.
     * Move generation for each piece won't include illegal captures,
     * i.e. when the player is trying to take their own piece.
     * @param piece Piece to move
     * @return If the piece is attempting to capture a piece
     */
    private boolean isMoveForCapture(Piece piece) {
        return (Board.currentPieceLocations[piece.getCoordinates().getToken()] != null);
    }

    /**
     * Ensure piece capture does not put/leave the player's king in check.
     * @param piece Piece to move
     * @return If the piece captured an enemy piece or not
     */
    private boolean handleCapture(Piece piece) {
        boolean isLegalMove;

        int move = piece.getCoordinates().getToken();

        //Save the piece we're attempting to capture
        Piece capturedPiece = Board.currentPieceLocations[move];
        Board.currentPieceLocations[move] = null;

        //Saves the "captured" pieces coordinates
        CoorPair capturedOldCoors = capturedPiece.getCoordinates();

        //Temporarily change this pieces coordinates
        Board.currentPieceLocations[oldCoors.getToken()] = null;
        Board.currentPieceLocations[move] = piece;

        //Check if king is in check
        isLegalMove = !isKingInCheck(piece.color);

        //Give pieces its coordinates back and restore pieces map
        capturedPiece.setCoordinates(capturedOldCoors.getXCoor(), capturedOldCoors.getYCoor());
        Board.currentPieceLocations[oldCoors.getToken()] = piece;
        Board.currentPieceLocations[capturedOldCoors.getToken()] = capturedPiece;

        //If this is a legal move we're capturing a piece
        if (isLegalMove) {
            //Remove piece from board
            Board.removePieceFromBoard(capturedPiece);
        }


        return isLegalMove;
    }

    /**
     * Check if the player is making a legal en-passant.
     *
     * @param piece Moved piece
     * @return If en passant legal
     */
    private boolean checkEnPassant(Piece piece) {
        CoorPair enemyCoor = new CoorPair(piece.getCoordinates());

        //Increment up/down space based on color
        enemyCoor.setYCoor(enemyCoor.getYCoor() + ((piece.color == Piece.Color.WHITE) ? 60.0 : -60.0));

        //Check for piece in this space
        if (Board.currentPieceLocations[enemyCoor.getToken()] != null) {
            Piece enemy;

            enemy = Board.currentPieceLocations[enemyCoor.getToken()];

            //Ensure enemy piece is a pawn
            if (!(enemy instanceof Pawn)) return false;

            //Only pawns of opposite color will be en-passantable here
            if (((Pawn) enemy).enPassantable) {
                //Remove captured piece
                //This is safe because we will never be able to en-passant and capture another piece at the same time
                Board.removePieceFromBoard(enemy);

                return true;
            }
        }

        return false;
    }

    /**
     * Checks if @pawn can promote, if it can promote
     * handle logic to replace it with a queen of the correct color.
     * @param pawn Pawn to check
     * @return If pawn has promoted or not
     */
    public boolean handlePawnPromotion(Piece pawn) throws FileNotFoundException {
        //Return false if pawn is not on the correct rank
        if (pawn.getYCoor() != 0 && pawn.getYCoor() != 420) return false;

        //Gets a new queen in the correct color
        Piece newQueen = (pawn.color == Piece.Color.WHITE) ? new Queen(Piece.Color.WHITE) : new Queen(Piece.Color.BLACK);

        //Create queen and add to board
        Board.anchorPane.getChildren().add(newQueen.pieceObject);
        Board.pieces.add(newQueen);
        newQueen.setCoordinates(pawn.getCoordinates());
        newQueen.draw();
        Board.currentPieceLocations[newQueen.getCoordinates().getToken()] = newQueen;
        setActions(newQueen);

        //Remove pawn from board
        Board.removePieceFromBoard(pawn);

        return true;
    }

    /**
     * Checks for and handles castling.
     * @return If the player has castled
     */
    public boolean handleCastle(Piece king) {
        //Check if we're castling
        for (Piece rook : Board.pieces.getPiecesByColor(king.color)) {
            //If not rook, continue looking for one
            if (rook.pieceType != Piece.PieceType.ROOK) continue;

            boolean castling = false;
            int rookXCoordinateOffset = -1;

            //Checking right rook
            if (rook.getCoordinates()
                    .coorEquals(new CoorPair(oldCoors.getXCoor() + 180, oldCoors.getYCoor()))) {
                //Checks if this move is trying to castle with right rook
                if (king.getCoordinates()
                        .coorEquals(
                                new CoorPair(oldCoors.getXCoor() + 120, oldCoors.getYCoor()))) {
                    castling = true;
                    rookXCoordinateOffset = -120;
                }
            }

            //Checking left rook
            if (rook.getCoordinates()
                    .coorEquals(new CoorPair(oldCoors.getXCoor() - 240, oldCoors.getYCoor()))) {
                //Checks if this move is trying to castle with left rook
                if (king.getCoordinates()
                        .coorEquals(
                                new CoorPair(oldCoors.getXCoor() - 120, oldCoors.getYCoor()))) {
                    castling = true;
                    rookXCoordinateOffset = 180;
                }
            }

            //If not castling continue looking
            if (!castling) continue;

            //Update location of rook
            Board.currentPieceLocations[rook.getCoordinates().getToken()] = null;
            rook.setCoordinates(rook.getXCoor() + rookXCoordinateOffset, rook.getYCoor());
            Board.currentPieceLocations[rook.getCoordinates().getToken()] = rook;

            //Play castling sound effect
            SoundControl.playCastling();

            //If we've castled there's no need to continue checking pieces
            break;
        }

        return true;
    }

    /**
     * Takes a players color and returns the opponents color.
     * @param color color of the player in question
     * @return oponents color
     */
    public static Piece.Color getOpponentColor(Piece.Color color) {
        return (color == Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;
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
        for( Piece piece : Board.pieces.getPiecesByColor(color)) {
            if ( piece instanceof King ) {
                kingToken = piece.getCoordinates().getToken();
            }
        }

        //Check all the moves for the opponent
        for ( Piece piece : Board.pieces.getPiecesByColor(getOpponentColor(color))) {
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
        for (Piece pieceToCheck : Board.pieces.getOpponentPieces(colorToCheck)) {
            //Find all potential moves
            potentialMovesBitBoard = pieceToCheck.findPotentialMoves();

            CoorPair oldPieceCoors = pieceToCheck.getCoordinates();

            //Test all potential moves
            for ( int i = 0; i < 64; i++ ) {
                if (BitBoard.compareToken(i, potentialMovesBitBoard)) {
                    pieceToCheck.setCoordinates(i);

                    if (isPotentialMoveLegal(pieceToCheck)) {
                        inCheckmate = false;
                        break;
                    }
                }
            }

            //Restore coordinates of piece we're checking
            pieceToCheck.setCoordinates(oldPieceCoors);

            //Exit loop if not mate
            if (!inCheckmate) break;
        }

        if (inCheckmate) System.out.println("Checkmate.");
    }
}