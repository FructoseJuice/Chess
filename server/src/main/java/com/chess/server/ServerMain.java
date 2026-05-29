package com.chess.server;

import com.chess.common.Networking.InitPayload;
import com.chess.common.Networking.Message;
import com.chess.common.Networking.MessageType;
import com.chess.common.Pieces.Piece;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class ServerMain {
    private static final int PORT = 5050;

    private static ClientHandler whiteClientHandler = null;
    private static ClientHandler blackClientHandler = null;

    public static void main(String[] args) {
        System.out.println("Starting Chess Server on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT, 0, InetAddress.getByName("0.0.0.0"))) {
            while (true) {
                System.out.println("Waiting on client...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler handler;
                if (whiteClientHandler == null) {
                    handler = new ClientHandler(clientSocket, Piece.Color.WHITE);
                    whiteClientHandler = handler;

                } else if (blackClientHandler == null) {
                    handler = new ClientHandler(clientSocket, Piece.Color.BLACK);
                    blackClientHandler = handler;

                } else {
                    System.out.println("Too many connections, rejecting");
                    continue;
                }

                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void disconnectClients() {
        try {
            whiteClientHandler.closeConnection();
            blackClientHandler.closeConnection();
        } catch (Exception ignored) {

        } finally {
            whiteClientHandler = null;
            blackClientHandler = null;
        }
    }

    // Forward message to other player
    public synchronized static void forwardClientMessage(Message msg, Piece.Color senderColor) {
        if (senderColor == Piece.Color.WHITE) {
            blackClientHandler.sendMessage(msg);
        } else {
            whiteClientHandler.sendMessage(msg);
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;
    private final Piece.Color color;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket clientSocket, Piece.Color color) {
        socket = clientSocket;
        this.color = color;
    }
    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Sending INIT to new client...");
            InitPayload messagePayload = new InitPayload(color);

            // Send initial handshake assigning player color
            sendMessage(new Message(MessageType.INIT, messagePayload));

            // Ensure client received color
            Message msg = (Message) in.readObject();
            while (msg.getType() != MessageType.INIT && ((InitPayload) msg.getPayload()).assignedPlayerColor != color) {
                // Resend assignment
                sendMessage(new Message(MessageType.INIT, messagePayload));
            }

            System.out.println("INIT received from client");

            while (true) {
                // Wait for move message from client
                msg = (Message) in.readObject();
                System.out.println("Received: " + msg);

                // Forward message to other player
                ServerMain.forwardClientMessage(msg, color);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected: " + socket.getInetAddress());
        } finally {
            closeConnection();
        }
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException ignored) {}

        ServerMain.disconnectClients();
    }

    public Piece.Color getColor() {
        return color;
    }
}
