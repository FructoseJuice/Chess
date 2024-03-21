import java.util.ArrayList;

public class PieceList {
    //Holds all pieces currently on the board
    private final ArrayList<Piece> whitePieces;
    private final ArrayList<Piece> blackPieces;

    public PieceList() {
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
    }

    public ArrayList<Piece> getAllPieces() {
        ArrayList<Piece> allPieces = new ArrayList<>();

        allPieces.addAll(whitePieces);
        allPieces.addAll(blackPieces);

        return allPieces;
    }

    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
    }

    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }

    public ArrayList<Piece> getPiecesByColor(Piece.Color color) {
        return (color == Piece.Color.WHITE) ? whitePieces : blackPieces;
    }

    public ArrayList<Piece> getOpponentPieces(Piece.Color color) {
        return (color == Piece.Color.WHITE) ? blackPieces : whitePieces;
    }

    public void add(Piece piece) {
        if (piece.color == Piece.Color.WHITE) {
            whitePieces.add(piece);
        } else {
            blackPieces.add(piece);
        }
    }

    public void remove(Piece piece) {
        if (piece.color == Piece.Color.WHITE) {
            whitePieces.remove(piece);
        } else {
            blackPieces.remove(piece);
        }
    }
}
