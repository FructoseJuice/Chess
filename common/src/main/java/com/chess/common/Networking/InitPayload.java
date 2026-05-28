package com.chess.common.Networking;

import com.chess.common.Pieces.Piece;

import java.io.Serializable;

public class InitPayload extends Payload implements Serializable {
    private static final long serialVersionUID = 1L;

    public final Piece.Color assignedPlayerColor;

    public InitPayload(Piece.Color assignedColor) {
        assignedPlayerColor = assignedColor;
    }
}
