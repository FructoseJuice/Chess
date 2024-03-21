package Utils;

import javafx.scene.media.AudioClip;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Plays all sound effects in game
 */
public class SoundControl {
    static final AudioClip GAME_START;
    static final AudioClip CAPTURE;
    static final AudioClip CASTLING;
    static final AudioClip CHECK;
    static final AudioClip MOVE;

    static {
        try {
            File startFile = new File("SFX\\Game_Start.wav");
            final File captureFile = new File("SFX\\Capture.wav");
            final File castlingFile = new File("SFX\\Castling.wav");
            final File checkFile = new File("SFX\\Check.wav");
            final File moveFile = new File("SFX\\Piece_Move.wav");

            GAME_START = new AudioClip(startFile.toURI().toURL().toString());
            CAPTURE = new AudioClip(captureFile.toURI().toURL().toString());
            CASTLING = new AudioClip(castlingFile.toURI().toURL().toString());
            CHECK = new AudioClip(checkFile.toURI().toURL().toString());
            MOVE = new AudioClip(moveFile.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void playCheck() {
        CHECK.play();
    }
    public static void playCapture() {
        CAPTURE.play();
    }
    public static void playCastling() {
        CASTLING.play();
    }
    public static void playMove() {
        MOVE.play();
    }
    public static void playGameStart() throws MalformedURLException {GAME_START.play();}
}
