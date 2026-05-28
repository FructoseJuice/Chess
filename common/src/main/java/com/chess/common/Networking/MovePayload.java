package com.chess.common.Networking;

import com.chess.common.Utils.CoorPair;

import java.io.Serializable;

public class MovePayload extends Payload implements Serializable {
    private static final long serialVersionUID = 1L;

    final public CoorPair from;
    final public CoorPair to;

    final public boolean capturedPiece;
    final public CoorPair capturedPieceCoors;

    public MovePayload(CoorPair from, CoorPair to) {
        this.from = from;
        this.to = to;

        this.capturedPiece = false;
        this. capturedPieceCoors = new CoorPair(-1, -1);
    }

    public MovePayload(CoorPair from, CoorPair to, boolean capturedPiece, CoorPair capturedPieceCoors) {
        this.from = from;
        this.to = to;

        this.capturedPiece = capturedPiece;
        this. capturedPieceCoors = capturedPieceCoors;
    }

    @Override
    public String toString() {
        return from.toString() + " -> " + to.toString() + " \nCapture: " + capturedPiece + "\n Captured Piece: " + capturedPieceCoors;
    }
}
