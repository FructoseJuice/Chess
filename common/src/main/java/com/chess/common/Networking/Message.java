package com.chess.common.Networking;

import com.chess.common.Pieces.Piece;

import java.io.Serializable;

// Must be Serializable to send over ObjectInputStream
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final MessageType type;
    private final Payload payload;

    public Message(MessageType type, Payload payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType() { return type; }
    public Payload getPayload() { return payload; }

    @Override
    public String toString() {
        return type + ": \n" + payload;
    }
}