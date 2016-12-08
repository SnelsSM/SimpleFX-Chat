package methods;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by snels on 27.11.2016.
 */
public class Audio extends Thread {
    private String type;
    private InputStream inputStream;

    public Audio(String type) {
        this.type = type;
        this.start();

    }

    public void run() {
        Clip c = null;
        AudioInputStream ais = null;

        try {
            c = AudioSystem.getClip();
            InputStream bufferedIS = new BufferedInputStream(getType(type));
            ais = AudioSystem.getAudioInputStream(bufferedIS);
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



    private InputStream getType(String type) {
        if (type.equals("sent")) {
            this.inputStream = getClass().getClassLoader().getResourceAsStream("sent.wav");
        }
        return this.inputStream;
    }
}
