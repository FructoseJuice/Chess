import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * //TODO: IMPLEMENT EN-PASSENT (BOOLEAN EN-PASSENTABLE IN PAWN CLASS)
 */

public class Main extends Application {
    public Main() throws FileNotFoundException, MalformedURLException {
    }

    //Sets up AnchorPane of piece images
    AnchorPane anchorPane = constructBoard();
    //Used for sound effects
    SoundControl soundControl = new SoundControl();
    //Used for checking the location and color of all pieces on board
    //Integer represents HashCode for coordinate pair
    public static HashMap<Integer, Piece.Color> currentPieceLocations = new HashMap<>();
    //Represents all pieces currently on the board
    public static List<Piece> allPieces = new ArrayList<>();
    //All queens that can be used for pawn promotion
    public static LinkedList<Piece> extraPieces = new LinkedList<>();

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

    //Creates all the pieces on the chessboard
    public void constructPieces() throws FileNotFoundException {
        //All white pieces on chessboard
        List<Piece> whitePieces = new ArrayList<>();
        //All black pieces on chessboard
        List<Piece> blackPieces = new ArrayList<>();
        //Creates new pieces that aren't pawns in both lists
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
        for (int yCoor = 0, xCoor = 0; xCoor < blackPieces.size(); xCoor++) {
            blackPieces.get(xCoor).setCoordinates(xCoor * 60, yCoor);
            currentPieceLocations.put(blackPieces.get(xCoor).getCoordinates().hashCode(), Piece.Color.BLACK);
        }
        for (int yCoor = 7, xCoor = 0; xCoor < whitePieces.size(); xCoor++) {
            whitePieces.get(xCoor).setCoordinates(xCoor * 60, yCoor * 60);
            currentPieceLocations.put(whitePieces.get(xCoor).getCoordinates().hashCode(), Piece.Color.WHITE);
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
            currentPieceLocations.put(whitePieces.get(i).getCoordinates().hashCode(), Piece.Color.WHITE);
            currentPieceLocations.put(blackPieces.get(i).getCoordinates().hashCode(), Piece.Color.BLACK);

            ++xCoor;
        }

        //Makes extra pieces
        for (int i = 0; i < 8; i++) {
            extraPieces.add(new Queen(Piece.Color.WHITE));
            extraPieces.add(new Queen(Piece.Color.BLACK));
        }
        allPieces.addAll(extraPieces);
        for (Piece extra : extraPieces) {
            extra.setCoordinates(-1000, -1000);
        }

        //Puts all pieces into combined list
        allPieces.addAll(whitePieces);
        allPieces.addAll(blackPieces);
    }


    /**
     * Checks if a king is in check in the current board state
     *
     * @param movedColor the color of the player who moved last
     * @return If opponent king is in check
     */
    public static boolean isKingInCheck(Piece.Color movedColor) {
        CoorPair kingCoordinates = new CoorPair(-1, -1);
                        /*
                        Checks if we put the opponent in check
                         */
        //Grabs the opponent kings Coordinates
        for (Piece opponentPiece : allPieces) {
            if (opponentPiece.color != movedColor & opponentPiece.pieceType == Piece.PieceType.KING) {
                kingCoordinates = opponentPiece.getCoordinates();
            }
        }

        HashSet<Integer> hashedCoordinates = new HashSet<>();

        for (Piece piece : allPieces) {
            if (piece.color == movedColor) {
                if (piece.pieceType == Piece.PieceType.PAWN) {
                    for (CoorPair legalMove : piece.movesForCheck()) {
                        hashedCoordinates.add(legalMove.hashCode());
                    }
                } else {
                    for (CoorPair legalMove : piece.findLegalMoves()) {
                        hashedCoordinates.add(legalMove.hashCode());
                    }
                }
            }
        }

        return hashedCoordinates.contains(kingCoordinates.hashCode());
    }

    //Logs a pieces old coordinates before being moved
    CoorPair oldCoors;
    ArrayList<CoorPair> legalMoves = new ArrayList<>();
    //Logs the current player to move
    Piece.Color playerToMove = Piece.Color.WHITE;

    public void setActions() {
        for (Piece piece : allPieces) {
            piece.pieceObject.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    piece.pieceObject.setCursor(Cursor.OPEN_HAND);
                }
            });

            piece.pieceObject.setOnMousePressed(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    //                   System.out.println("PLAYER TO MOVE: " + playerToMove);
                    //Logs old coordinates
                    oldCoors = new CoorPair(piece.getXCoor(), piece.getYCoor());

                    piece.pieceObject.setCursor(Cursor.CLOSED_HAND);
                    piece.pieceObject.toFront();

                    //Find all legal moves for chosen piece
                    legalMoves = piece.findLegalMoves();
                }
            });

            piece.pieceObject.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    piece.pieceObject.setCursor(Cursor.CLOSED_HAND);
                    //Must adjust coordinates by 30 before "attaching" to mouse
                    piece.setXCoor(event.getSceneX() - 30);
                    piece.setYCoor(event.getSceneY() - 30);
                    piece.draw();
                }
            });

            piece.pieceObject.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    piece.pieceObject.setCursor(Cursor.OPEN_HAND);

                    //For SFX control
                    boolean playedSFX = false;
                    boolean playCapture = false;
                    //Snaps piece to nearest space to cursor
                    piece.findNearestSpace();

                    boolean isLegalMove = false;


                    for (CoorPair legal : legalMoves) {
                        System.out.println(legal.toString());
                    }
                    System.out.println();


                    //Checks if this move is potentially legal
                    for (CoorPair legal : legalMoves) {
                        if (piece.getCoordinates().hashCode() == legal.hashCode() & playerToMove == piece.color) {
                            isLegalMove = true;
                        }
                    }


                    //Checks if this move puts player to move's king in check
                    //If this move puts the king in check, then it's illegal
                    boolean injectedCoordinates = false;
                    CoorPair kingCoordinates = new CoorPair(-1, -1);
                    //Gets the coordinates of our king
                    for (Piece potentialKing : Main.allPieces) {
                        if (potentialKing.color == piece.color & potentialKing.pieceType == Piece.PieceType.KING) {
                            kingCoordinates = potentialKing.getCoordinates();
                            break;
                        }
                    }

                    if (piece.pieceType != Piece.PieceType.KING) {
                        //Removes original coordinate of piece, so we can check it's new potential position
                        currentPieceLocations.remove(oldCoors.hashCode());
                        //If there's not a piece at these coordinates we can inject our new coordinates in and be safe
                        if (!currentPieceLocations.containsKey(piece.getCoordinates().hashCode())) {
                            injectedCoordinates = true;
                            currentPieceLocations.put(piece.getCoordinates().hashCode(), piece.color);
                        }

                        for (Piece opponentPiece : Main.allPieces) {
                            if (opponentPiece.color != piece.color & opponentPiece.pieceType != Piece.PieceType.KING) {
                                //Checks the moves of all opponent pieces
                                if (opponentPiece.pieceType != Piece.PieceType.PAWN) {
                                    for (CoorPair move : opponentPiece.findLegalMoves()) {
                                        if (kingCoordinates.coorEquals(move)
                                                & !(piece.getCoordinates().coorEquals(opponentPiece.getCoordinates()))) {
                                            isLegalMove = false;
                                        }
                                    }
                                } else {
                                    for (CoorPair move : opponentPiece.movesForCheck()) {
                                        if (kingCoordinates.coorEquals(move)
                                                & !(piece.getCoordinates().coorEquals(opponentPiece.getCoordinates()))) {
                                            isLegalMove = false;
                                        }
                                    }
                                }
                            }
                        }

                        //If we injected our new coordinates, we have to remove them again and put back old coordinates
                        if (injectedCoordinates) {
                            Main.currentPieceLocations.remove(piece.getCoordinates().hashCode());
                        }
                        Main.currentPieceLocations.put(oldCoors.hashCode(), piece.color);
                    }


                    //Checks if a piece in the space player is trying to move to
                    //We know that if it's a legal move then we're capturing this piece
                    if (currentPieceLocations.containsKey(piece.getCoordinates().hashCode()) &
                            isLegalMove) {
                        for (Piece capturedPiece : allPieces) {
                            if (capturedPiece != piece &
                                    capturedPiece.coorPair.coorEquals(piece.getCoordinates())) {
                                playCapture = true;
                                capturedPiece.setCoordinates(-1000, -1000);
                                currentPieceLocations.remove(capturedPiece.getCoordinates().hashCode());
                                allPieces.remove(capturedPiece);
                                break;
                            }
                        }
                    }


                    if (!isLegalMove) {
                        //Bounces piece back to its original coordinates if it's not a legal move
                        piece.setCoordinates(oldCoors.getxCoor(), oldCoors.getyCoor());
                        playedSFX = true;

                    } else {
                        currentPieceLocations.remove(oldCoors.hashCode());

                        if (isKingInCheck(piece.color)) {
                            for (Piece king : allPieces) {
                                if (king.pieceType == Piece.PieceType.KING & king.color != piece.color) {
                                    System.out.println("PUT " + king.color + " IN CHECK!");
                                    soundControl.playCheck();
                                    playedSFX = true;
                                    playCapture = false;
                                }
                            }
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

                            if (piece.color == Piece.Color.BLACK) {
                                if (piece.getYCoor() == 420) {
                                    extraPieces.getLast().setCoordinates(piece.getXCoor(), 420);
                                    piece.setCoordinates(-1000, -1000);
                                    currentPieceLocations.put(extraPieces.getLast().getCoordinates().hashCode(), Piece.Color.BLACK);
                                    allPieces.add(extraPieces.removeLast());
                                    allPieces.remove(piece);
                                }
                            } else {
                                if (piece.getYCoor() == 0) {
                                    extraPieces.getFirst().setCoordinates(piece.getXCoor(), 0);
                                    piece.setCoordinates(-1000, -1000);
                                    currentPieceLocations.put(extraPieces.getFirst().getCoordinates().hashCode(), Piece.Color.WHITE);
                                    allPieces.add(extraPieces.removeFirst());
                                    allPieces.remove(piece);
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
                                                currentPieceLocations.remove(rook.getCoordinates().hashCode());
                                                rook.setCoordinates(oldCoors.getxCoor() + 60, oldCoors.getyCoor());
                                                currentPieceLocations.put(rook.getCoordinates().hashCode(), rook.color);
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
                                                currentPieceLocations.remove(rook.getCoordinates().hashCode());
                                                rook.setCoordinates(oldCoors.getxCoor() - 60, oldCoors.getyCoor());
                                                currentPieceLocations.put(rook.getCoordinates().hashCode(), rook.color);
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
                        currentPieceLocations.put(piece.getCoordinates().hashCode(), piece.color);

                        piece.draw();
                    }
                }
            });
        }
    }

}