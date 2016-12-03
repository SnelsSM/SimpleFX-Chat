package methods;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by snels on 27.11.2016.
 */
public class Audio extends Thread {
    private String type;

    public Audio(String type) {
        this.type = type;
        this.start();

    }

    public void run() {
        Clip c = null;
        AudioInputStream ais = null;

        try {
            c = AudioSystem.getClip();
            ais = AudioSystem.getAudioInputStream(new File(getType(type)));
            c.open(ais);
            c.loop(0);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private String getType(String type) {
        if (type.equals("sent")) this.type = "client/src/sent.wav";
        return this.type;
    }
}
