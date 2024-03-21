import javafx.scene.layout.AnchorPane;

import java.io.FileNotFoundException;

/**
 * Represents the chess board
 * Holds all pieces and AnchorPane
 */
public class Board {
    //Used for checking the location and color of all pieces on board
    //Integer represents token of coordinate pair
    //Start squares from index 1
    public static final Piece[] currentPieceLocations = new Piece[64];

    //AnchorPane of piece images
    public static final AnchorPane anchorPane;

    public static PieceList pieces;

    static {
        try {
            anchorPane = constructBoard();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds all pieces to anchorPane
     * @return AnchorPane with pieces
     */
    private static AnchorPane constructBoard() throws FileNotFoundException {
        //Make pieces and set actions on every piece
        constructPieces();

        //Makes AnchorPane for spaces
        AnchorPane spaces = new AnchorPane();
        spaces.setMaxSize(480, 480);

        //Add every active piece to AnchorPane
        for ( Piece piece : pieces.getAllPieces() ) {
            spaces.getChildren().add(piece.pieceObject);
        }

        return spaces;
    }


    /**
     * Creates all the pieces on the chessboard
     */
    private static void constructPieces() throws FileNotFoundException {
        pieces = new PieceList();

        //Create new pieces that aren't pawns in order for both lists
        pieces.add(new Rook(Piece.Color.WHITE));
        pieces.add(new Knight(Piece.Color.WHITE));
        pieces.add(new Bishop(Piece.Color.WHITE));
        pieces.add(new Queen(Piece.Color.WHITE));
        pieces.add(new King(Piece.Color.WHITE));
        pieces.add(new Bishop(Piece.Color.WHITE));
        pieces.add(new Knight(Piece.Color.WHITE));
        pieces.add(new Rook(Piece.Color.WHITE));

        pieces.add(new Rook(Piece.Color.BLACK));
        pieces.add(new Knight(Piece.Color.BLACK));
        pieces.add(new Bishop(Piece.Color.BLACK));
        pieces.add(new Queen(Piece.Color.BLACK));
        pieces.add(new King(Piece.Color.BLACK));
        pieces.add(new Bishop(Piece.Color.BLACK));
        pieces.add(new Knight(Piece.Color.BLACK));
        pieces.add(new Rook(Piece.Color.BLACK));


        //Set coordinates for each piece that's not a pawn
        for ( int xCoor = 0; xCoor < 8; xCoor++ ) {
            pieces.getBlackPieces().get(xCoor).setCoordinates(xCoor * 60, 0);
            currentPieceLocations[pieces.getBlackPieces().get(xCoor).getCoordinates().getToken()] = pieces.getBlackPieces().get(xCoor);

            pieces.getWhitePieces().get(xCoor).setCoordinates(xCoor * 60, 420);
            currentPieceLocations[pieces.getWhitePieces().get(xCoor).getCoordinates().getToken()] = pieces.getWhitePieces().get(xCoor);
        }

        //Adds all pawns to end of lists
        for (int i = 0; i < 8; i++) {
            pieces.getWhitePieces().add(new Pawn(Piece.Color.WHITE));
            pieces.getBlackPieces().add(new Pawn(Piece.Color.BLACK));
        }

        //Set coordinates for each pawn
        for (int xCoor = 0, i = 8; i < pieces.getWhitePieces().size(); i++, xCoor++) {
            pieces.getBlackPieces().get(i).setCoordinates(xCoor * 60, 60);
            pieces.getWhitePieces().get(i).setCoordinates(xCoor * 60, 360);

            currentPieceLocations[pieces.getWhitePieces().get(i).getCoordinates().getToken()] = pieces.getWhitePieces().get(i);
            currentPieceLocations[pieces.getBlackPieces().get(i).getCoordinates().getToken()] = pieces.getBlackPieces().get(i);
        }
    }


    public static void removePieceFromBoard(Piece piece) {
        pieces.getAllPieces().remove(piece);
        currentPieceLocations[piece.getCoordinates().getToken()] = null;
        anchorPane.getChildren().remove(piece.pieceObject);
    }
}
