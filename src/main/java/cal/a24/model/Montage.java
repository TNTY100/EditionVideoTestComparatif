package cal.a24.model;

import javafx.scene.image.Image;
import lombok.Data;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import java.util.List;

@Data
public class Montage {

    public List<Segment> segments;

    public Montage(List<Segment> segments) {
        setSegments(segments);
    }

    public Image getImageFXAtTimeStamp(long timestamp) {
        if (timestamp < 0 || timestamp > getDureeTotale()) {
            throw new RuntimeException("Le timestamp est à l'extérieur des limites");
        }

        for (Segment segment: segments) {
            if (timestamp < segment.getDuree()) {
                System.out.println("Getting picture at " + timestamp);
                try {
                    return segment.getImageFXAtTimestampInContent(timestamp);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            timestamp -= segment.getDuree();
        }
        return null;
    }

    public long getDureeTotale() {
            return segments.stream().mapToLong(Segment::getDuree).sum();
    }

    public void export(String fileName) {
        try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(fileName, 3840, 2160)) {
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // or use grabber.getVideoCodec()
            recorder.setFormat("mov,mp4,m4a,3gp,3g2,mj2");
            recorder.setFrameRate(60);
            recorder.setVideoBitrate(1184091);

            // Set audio settings if the input video has audio
            recorder.setAudioChannels(2);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);  // AAC is commonly used
            recorder.setSampleRate(48000);
            recorder.setAudioBitrate(326757);

            recorder.start();

            for (Segment segment : segments) {
                recorder.record(segment.startGrab());
                Frame frame;
                while ((frame = segment.grab()) != null) {
                    recorder.record(frame);
                }
            }

            recorder.stop();
        } catch (FrameRecorder.Exception e) {
            throw new RuntimeException(e);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
    }
}
