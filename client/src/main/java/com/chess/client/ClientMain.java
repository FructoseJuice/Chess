package com.chess.client;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import com.chess.common.Networking.*;
import com.chess.common.Pieces.*;
import com.chess.common.Utils.Board;
import com.chess.common.Utils.CoorPair;
import com.chess.common.Utils.BitBoard;
import com.chess.common.Utils.ImageLoader;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ClientMain extends Application implements ClientNetworkManager.OnMessageListener {
    /*
    GLOBAL VARIABLES
     */
    //Saves a pieces old coordinates before being moved
    CoorPair movedPieceOldCoors;
    //Saves the legal moves of a piece
    long potentialMovesBitBoard = 0L;

    public static Piece.Color myColor;
    //Logs the current player to move
    Piece.Color playerToMove = Piece.Color.WHITE;
    //If game is in checkmate status
    boolean inCheckmate = false;


    private ClientNetworkManager networkManager;
    private static AtomicBoolean isMyTurn = new AtomicBoolean(false);


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Connect to Server
     * Display board and pieces
     * Call setAction to respond to user input
     */
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        //Sets up the background chess board
        ImageView boardView = ImageLoader.loadImage("Chess_Board");
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

        // Connect to server
        connectToServer();
    }

    public static void setMyColor(Piece.Color color) {
        myColor = color;

        if (color == Piece.Color.WHITE) {
            isMyTurn.set(true);
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
            movedPieceOldCoors = new CoorPair(piece.getXCoor(), piece.getYCoor());

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
            handlePieceMovement(piece, potentialMovesBitBoard);
        });
    }

    /**
     * Handles all the main logic for moving a piece.
     * @param movedPiece main.Piece moved by player
     */
    public void handlePieceMovement(Piece movedPiece, long movedPiecePotentialMoves) {
        boolean playedSFX = false;
        boolean playCapture = false;
        boolean promoted = false;
        boolean isLegalMove;
        CoorPair capturedPieceCoors = new CoorPair(-1, -1);

        //Find the nearest space to the cursor
        movedPiece.findNearestSpace();

        //Log move
        CoorPair move = movedPiece.getCoordinates();

        //Reset coordinates
        movedPiece.setCoordinates(movedPieceOldCoors);

        //Check if this move is legal
        isLegalMove = isPotentialMoveLegal(movedPiece, movedPieceOldCoors , move, movedPiecePotentialMoves);

        //If legal capture, remove piece from board
        if (isLegalMove) {
            if (Board.isSpaceOccupied(move)) {
                Board.removePieceFromBoard(Board.currentPieceLocations[move.getToken()]);
                capturedPieceCoors = move;

                playCapture = true;
            }
        }

        //Check if we're making a legal en passant
        if (isLegalMove) {
            if (movedPiece instanceof Pawn && checkEnPassant((Pawn) movedPiece)) {
                CoorPair capturedPawnCoordinates = new CoorPair(move.getXCoor(), move.getYCoor());
                capturedPieceCoors = capturedPawnCoordinates;
                int yOffset = (movedPiece.color == Piece.Color.WHITE) ? 60 : -60;
                capturedPawnCoordinates.setYCoor(move.getYCoor() + yOffset);

                Board.removePieceFromBoard(Board.currentPieceLocations[capturedPawnCoordinates.getToken()]);

                playCapture = true;
            }
        }

        if (!isLegalMove) {
            //Short circuits logic if not a legal move
            movedPiece.draw();
            return;
        }

        //Switch the turn since this is a legal move
        if (playerToMove == Piece.Color.WHITE) {
            playerToMove = Piece.Color.BLACK;
        } else {
            playerToMove = Piece.Color.WHITE;
        }

        //Remove piece from old coordinates
        Board.removePieceFromSpace(movedPieceOldCoors);

        //If a pawn moves we need to set first move to false and check for piece promotion
        if (movedPiece instanceof Pawn) {
            //Checks for and handles pawn promotion
            try {
                promoted = handlePawnPromotion(movedPiece, move);
            } catch (FileNotFoundException e) {
                //Occurs when new piece doesn't have an image on file
                throw new RuntimeException(e);
            }

            if (!promoted) movedPiece.setCoordinates(move);
            ((Pawn) movedPiece).setFirstMoveFalse();
        }

        if (!promoted) {
            //If we haven't promoted, update piece locations with new move
            Board.addPieceToSpace(movedPiece, move);
            movedPiece.setCoordinates(move);
        }

        //Check if opponent king is now in check
        if (isKingInCheck(getOpponentColor(movedPiece.color))) {
            //SoundControl.playCheck();

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
            if (((King) movedPiece).isFirstMove() && handleCastle(movedPiece, movedPieceOldCoors)) {
                //SoundControl.playCastling();

                playedSFX = true;
            }

            //Set first move false
            ((King) movedPiece).setFirstMoveFalse();
        }

        //Clear pawn en-passantable booleans
        for (Piece pawn : Board.pieces.getAllPieces()) {
            if (pawn instanceof Pawn && pawn != movedPiece) {
                ((Pawn) pawn).enPassantable = false;
            }
        }

        //Check if we need to play the capture sound effect
        if (playCapture) {
            //SoundControl.playCapture();

            playedSFX = true;
        }

        //If we haven't played a sound effect, we need to play the move sound effect
        if (!playedSFX) {
            //SoundControl.playMove();
        }

        //Draw new location to board
        movedPiece.draw();

        //Check if player has put the opponent in checkmate
        inCheckmate(movedPiece.color);

        // Send move to opponent
        MovePayload movePayload = new MovePayload(
                movedPieceOldCoors,
                move,
                playCapture,
                capturedPieceCoors
        );

        networkManager.sendMove(movePayload);
    }

    /**
     * Takes a pieces coordinates, checks if this move
     * puts the players king into check, or takes the players king out of check.
     * Also checks for en-passant.
     *
     * @param piece piece that is being moved
     * @return if the move being made is legal
     */
    private boolean isPotentialMoveLegal(Piece piece, CoorPair oldCoors, CoorPair move, long potentialMoves) {
        boolean isLegalMove = false;

        /*
        Check if this move is potentially legal
         */
        //Check if it's this player's turn
        if (!isMyTurn.get()) return false;
        //if (playerToMove != piece.color) return false;

        //Check if desired move is potentially legal
        if (!BitBoard.compareToken(move.getToken(), potentialMoves)) return false;

        /*
        Checks if this move puts player to move's king in check
        if this move puts their king in check, then it's illegal.
         */

        /*
        Check if this is a legal non-capture move
         */
        if (!Board.isSpaceOccupied(move)) {
            //Removes original coordinate of piece, so we can check it's new potential position
            Board.removePieceFromSpace(oldCoors);

            //Put new coordinates in
            Board.addPieceToSpace(piece, move);
            piece.setCoordinates(move);

            //Not a legal move is king is now in check
            isLegalMove = !isKingInCheck(piece.color);

            //Remove new coordinates
            Board.removePieceFromSpace(move);

            //Restore old coordinates
            Board.addPieceToSpace(piece, oldCoors);
            piece.setCoordinates(oldCoors);

        /*
        Check if this is a legal capture
         */
        } else if (Board.isSpaceOccupied(move)) {
            isLegalMove = isCaptureLegal(piece, oldCoors, move);

        /*
        Check if this is a legal en-passant
         */
        } else if (piece instanceof Pawn && checkEnPassant((Pawn) piece)) {
            int yOffset = (piece.color == Piece.Color.WHITE) ? -60 : 60;

            move.setYCoor(move.getYCoor() + yOffset);

            isLegalMove = isCaptureLegal(piece, oldCoors, move);
        }

        return isLegalMove;
    }

    private boolean isCaptureLegal(Piece piece, CoorPair oldCoors, CoorPair move) {
        /*
            If we're capturing, we want to temporarily replace the captured piece
            with our own, and check if the king is in check afterwards.
        */

        //Save the piece we're attempting to capture
        Piece capturedPiece = Board.getPieceAtSpace(move);

        //Saves the "captured" pieces coordinates
        CoorPair capturedOldCoors = capturedPiece.getCoordinates();

        //Temporarily change this pieces coordinates
        Board.removePieceFromSpace(oldCoors);
        Board.addPieceToSpace(piece, move);

        //Set coordinates to prevent move calculation
        capturedPiece.setCoordinates(new CoorPair(-1000, -1000));

        //Check if king is in check
        boolean isLegalMove = !isKingInCheck(piece.color);

        //Give pieces its coordinates back and restore pieces map
        capturedPiece.setCoordinates(capturedOldCoors.getXCoor(), capturedOldCoors.getYCoor());
        Board.addPieceToSpace(piece, oldCoors);
        Board.addPieceToSpace(capturedPiece, capturedOldCoors);

        return isLegalMove;
    }

    /**
     * Check if the player is making a legal en-passant.
     *
     * @param piece Moved piece
     * @return If en passant legal
     */
    private boolean checkEnPassant(Pawn piece) {
        CoorPair enemyCoordinatesLeft = new CoorPair(piece.getCoordinates());
        CoorPair enemyCoordinatesRight = new CoorPair(piece.getCoordinates());

        if ((piece.color == Piece.Color.WHITE && piece.getCoordinates().getRow() != 3) ||
                (piece.color == Piece.Color.BLACK && piece.getCoordinates().getRow() != 4)) {
            return false;
        }

        //Increment left/right space
        enemyCoordinatesLeft.setXCoor(piece.getXCoor() - 60);
        enemyCoordinatesRight.setXCoor(piece.getXCoor() + 60);

        Piece enemy = null;

        //Check for piece in this space
        if (Board.isSpaceOccupied(enemyCoordinatesLeft)) {
            //Grab detected enemy
            enemy = Board.getPieceAtSpace(enemyCoordinatesLeft);
        } else if (Board.isSpaceOccupied(enemyCoordinatesRight)) {
            //Grab detected enemy
            enemy = Board.getPieceAtSpace(enemyCoordinatesRight);
        }

        //If no enemy found, return false
        if (enemy == null) return false;

        //Ensure enemy piece is a pawn
        if (!(enemy instanceof Pawn)) return false;

        //Only pawns of opposite color will be en-passantable here
        return ((Pawn) enemy).enPassantable;
    }

    /**
     * Checks if @pawn can promote, if it can promote
     * handle logic to replace it with a queen of the correct color.
     * @param pawn main.Pawn to check
     * @return If pawn has promoted or not
     */
    public boolean handlePawnPromotion(Piece pawn, CoorPair move) throws FileNotFoundException {
        //Return false if pawn is not on the correct rank
        if (move.getRow() != 0 && move.getRow() != 7) return false;

        //Gets a new queen in the correct color
        Piece newQueen = (pawn.color == Piece.Color.WHITE) ? new Queen(Piece.Color.WHITE) : new Queen(Piece.Color.BLACK);

        //Create queen and add to board
        Board.anchorPane.getChildren().add(newQueen.pieceObject);
        Board.pieces.add(newQueen);
        newQueen.setCoordinates(move);
        newQueen.draw();
        Board.addPieceToSpace(newQueen, move);
        setActions(newQueen);

        //Remove pawn from board
        Board.removePieceFromBoard(pawn);

        return true;
    }

    /**
     * Checks for and handles castling.
     * @return If the player has castled
     */
    public boolean handleCastle(Piece king, CoorPair kingOldCoors) {
        //Check if we're castling
        for (Piece rook : Board.pieces.getPiecesByColor(king.color)) {
            //If not rook, continue looking for one
            if (rook.pieceType != Piece.PieceType.ROOK) continue;

            boolean castling = false;
            int rookXCoordinateOffset = -1;

            //Checking right rook
            if (rook.getCoordinates()
                    .coorEquals(new CoorPair(kingOldCoors.getXCoor() + 180, kingOldCoors.getYCoor()))) {
                //Checks if this move is trying to castle with right rook
                if (king.getCoordinates()
                        .coorEquals(
                                new CoorPair(kingOldCoors.getXCoor() + 120, kingOldCoors.getYCoor()))) {
                    castling = true;
                    rookXCoordinateOffset = -120;
                }
            }

            //Checking left rook
            if (rook.getCoordinates()
                    .coorEquals(new CoorPair(kingOldCoors.getXCoor() - 240, kingOldCoors.getYCoor()))) {
                //Checks if this move is trying to castle with left rook
                if (king.getCoordinates()
                        .coorEquals(
                                new CoorPair(kingOldCoors.getXCoor() - 120, kingOldCoors.getYCoor()))) {
                    castling = true;
                    rookXCoordinateOffset = 180;
                }
            }

            //If not castling continue looking
            if (!castling) continue;

            //Update location of rook
            Board.removePieceFromSpace(rook.getCoordinates());
            rook.setCoordinates(rook.getXCoor() + rookXCoordinateOffset, rook.getYCoor());
            Board.addPieceToSpace(rook, rook.getCoordinates());

            //Play castling sound effect
            //SoundControl.playCastling();

            //If we've castled there's no need to continue checking pieces
            break;
        }

        return false;
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

        long potentialMovesForPiece;

        //Check if opponent can make any legal moves
        for (Piece pieceToCheck : Board.pieces.getOpponentPieces(colorToCheck)) {
            //Find all potential moves
            potentialMovesForPiece = pieceToCheck.findPotentialMoves();

            CoorPair oldPieceCoors = pieceToCheck.getCoordinates();
            CoorPair move;

            //Test all potential moves
            for ( int i = 0; i < 64; i++ ) {
                if (BitBoard.compareToken(i, potentialMovesForPiece)) {
                    move = new CoorPair(60 * (i % 8), 60 * Math.floor(i / 8f));
                    if (isPotentialMoveLegal(pieceToCheck, oldPieceCoors, move, potentialMovesForPiece)) {
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


    /*
    Networking
     */
    public void connectToServer() {
        networkManager = new ClientNetworkManager(
                "chess.moonstream.stream",
                5050,
                this
        );
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Application stopped, closing network...");
        if (networkManager != null) {
            networkManager.close();
        }
        super.stop();
    }

    @Override
    public void onMessageReceived(Message msg) {
        // Make move on board
        MovePayload move = (MovePayload) msg.getPayload();

        Piece movedPiece = Board.getPieceAtSpace(move.from);

        if (move.capturedPiece) {
            // Remove captured piece
            Board.removePieceFromBoard(Board.getPieceAtSpace(move.capturedPieceCoors));
        }

        Board.removePieceFromSpace(move.from);
        Board.addPieceToSpace(movedPiece, move.to);

        movedPiece.setCoordinates(move.to);
        movedPiece.draw();

        // Update turn
        isMyTurn.set(true);
    }

    @Override
    public void onDisconnected() {
        networkManager.close();
    }

    @Override
    public void onError(String errorMessage) {
        System.err.println(errorMessage);
        System.exit(1);
    }
}

class ClientNetworkManager implements Runnable {
    private final String host;
    private final int port;
    private final OnMessageListener listener;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean running = true;

    public interface OnMessageListener {
        void onMessageReceived(Message msg);
        void onDisconnected();
        void onError(String errorMessage);
    }

    public ClientNetworkManager(String host, int port, OnMessageListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;

        // Start connection in a background thread
        new Thread(this, "NetworkThread").start();
    }

    @Override
    public void run() {
        try {
            // Resolve hostname to IP (handles domains like chess.example.com)
            InetAddress address = InetAddress.getByName(host);
            System.out.println("Connecting to host <" + host + "> : <" + address.getHostAddress() + ">");
            socket = new Socket(address, port);
            //socket = new Socket("192.168.50.9", port);

            // Initialize streams
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Notify success
            System.out.println("Connected to " + host + ":" + port);

            // Recieve init message
            Object obj;
            try {
                obj = in.readObject();

                InitPayload payload = (InitPayload) ((Message) obj).getPayload();

                // Save player color
                ClientMain.setMyColor(payload.assignedPlayerColor);

                // Send back init
                send(MessageType.INIT, new InitPayload(payload.assignedPlayerColor));
            } catch (IOException ignored ) {
                System.err.println("Invalid data received from server.");
                System.exit(1);
            }
        } catch (UnknownHostException e) {
            javafx.application.Platform.runLater(() ->
                    listener.onError("Unknown Host: " + host)
            );
        } catch (IOException e) {
            javafx.application.Platform.runLater(() ->
                    listener.onError("Connection Failed: " + e.getMessage())
            );
        } catch (ClassNotFoundException e) {
            javafx.application.Platform.runLater(() ->
                    listener.onError("Invalid message format received.")
            );
        }
    }

    /**
     * Sends a move or other data to the server.
     * @param type The type of message (MOVE, CHECK, etc.)
     * @param payload The data (e.g., "e2-e4")
     */
    public void send(MessageType type, Payload payload) {
        if (!running || out == null) {
            System.err.println("Cannot send: Not connected.");
            return;
        }
        try {
            out.writeObject(new Message(type, payload));
            out.flush();
        } catch (IOException e) {
            System.err.println("Send failed: " + e.getMessage());
            close();
        }
    }

    /**
     * Convenience method for sending moves.
     */
    public void sendMove(Payload moveData) {
        send(MessageType.MOVE, moveData);
    }

    public void close() {
        running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}