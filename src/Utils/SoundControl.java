package Utils;

import javafx.scene.media.AudioClip;

import java.net.MalformedURLException;
import java.net.URL;

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
        GAME_START = loadAudioClip("Game_Start.wav");
        CAPTURE = loadAudioClip("Capture.wav");
        CASTLING = loadAudioClip("Castling.wav");
        CHECK = loadAudioClip("Check.wav");
        MOVE = loadAudioClip("Piece_Move.wav");
    }

    private static AudioClip loadAudioClip(String fileName) {
        AudioClip audioClip = null;

        // Try loading from the filesystem first
        try {
            // Load from the filesystem (works during development)
            URL fileURL = new URL("file:resources/SFX/" + fileName);
            audioClip = new AudioClip(fileURL.toExternalForm());
        } catch (Exception e) {
            // If filesystem loading fails, try to load from the classpath
            try {
                URL resourceURL = SoundControl.class.getResource("/SFX/" + fileName);
                if (resourceURL == null) {
                    throw new RuntimeException("Sound file not found: " + fileName);
                }
                audioClip = new AudioClip(resourceURL.toExternalForm());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (audioClip == null) {
            throw new RuntimeException("Sound could not be loaded: " + fileName);
        }

        return audioClip;
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
