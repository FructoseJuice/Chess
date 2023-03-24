import javafx.scene.media.AudioClip;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Plays all sound effects in game
 */
public class SoundControl {
    final AudioClip capture;
    final AudioClip castling;
    final AudioClip check;
    final AudioClip move;

    public SoundControl() throws MalformedURLException {
        final File captureFile = new File("SFX\\Capture.wav");
        final File castlingFile = new File("SFX\\Castling.wav");
        final File checkFile = new File("SFX\\Check.wav");
        final File moveFile = new File("SFX\\Piece_Move.wav");

        capture = new AudioClip(captureFile.toURI().toURL().toString());
        castling = new AudioClip(castlingFile.toURI().toURL().toString());
        check = new AudioClip(checkFile.toURI().toURL().toString());
        move = new AudioClip(moveFile.toURI().toURL().toString());
    }

    public void playCheck() {
        check.play();
    }
    public void playCapture() {
        capture.play();
    }
    public void playCastling() {
        castling.play();
    }
    public void playMove() {
        move.play();
    }
    public void playGameStart() throws MalformedURLException {
        File startFile = new File("SFX\\Game_Start.wav");
        AudioClip start = new AudioClip(startFile.toURI().toURL().toString());
        start.play();
    }
}
