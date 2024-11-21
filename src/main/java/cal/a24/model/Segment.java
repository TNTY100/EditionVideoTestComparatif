package cal.a24.model;

import javafx.scene.image.Image;
import lombok.Data;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.JavaFXFrameConverter;

import java.io.Closeable;
import java.io.IOException;

@Data
public class Segment implements Closeable {

    public static JavaFXFrameConverter converter = new JavaFXFrameConverter();

    private FFmpegFrameGrabber grabber;

    private long timestampDebut;
    private long timestampFin;

    public Segment(String fichier) throws FFmpegFrameGrabber.Exception {
        grabber = new FFmpegFrameGrabber(fichier);
        grabber.start();

        timestampDebut = 0;
        grabber.setVideoTimestamp(timestampDebut);
        timestampFin = grabber.getLengthInTime();
    }

    public Segment(String fichier, long debut, long fin) throws FFmpegFrameGrabber.Exception {
        grabber = new FFmpegFrameGrabber(fichier);
        grabber.start();

        timestampDebut = debut;
        timestampFin = fin;
        grabber.setVideoTimestamp(timestampDebut);
    }


    public long getDuree() {
        return timestampFin - timestampDebut;
    }

    public Image getImageFXAtTimestampInContent(long timestamp) throws FFmpegFrameGrabber.Exception {
        timestamp = timestampDebut + timestamp;
        if (timestamp < 0 || timestamp > timestampFin) {
            throw new RuntimeException("Le timestamp ne fait pas parti de l'intervalle voulue");
        }

        System.out.println("Getting picture at in segement " + timestamp);
        grabber.setVideoTimestamp(timestamp);

        return converter.convert(grabber.grabImage());
    }

    public Frame startGrab() throws FrameGrabber.Exception {
        grabber.setVideoTimestamp(timestampDebut);
        return grab();
    }

    public Frame grab() throws FrameGrabber.Exception {
        if (grabber.getTimestamp() > timestampFin) {
            return null;
        }
        return grabber.grabFrame();
    }

    @Override
    public void close() throws IOException {
        grabber.stop();
        grabber.close();
    }
}
