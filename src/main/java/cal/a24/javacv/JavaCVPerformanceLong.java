package cal.a24.javacv;

import cal.a24.model.Montage;
import cal.a24.model.Segment;
import lombok.SneakyThrows;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;


public class JavaCVPerformanceLong {

    @SneakyThrows
    public static void main(String[] args) throws FFmpegFrameGrabber.Exception {

        String inputFile1 = "./src/main/resources/videos/bbb_sunflower_1080p_30fps_normal.mp4";
        String inputFile2 = "./src/main/resources/videos/48SecondesAvecSon.mp4";

        String outputFile = "./output/javacv_performance_complexe.mp4";

        Instant start = Instant.now();

        Segment segment1 = new Segment(inputFile1);
        Segment segment2 = new Segment(inputFile2);

        List<Segment> segments = List.of(segment1, segment2, segment1);
        Montage montage = new Montage(segments);

        FFmpegFrameGrabber grabber = segment1.getGrabber();
        montage.export(outputFile, grabber.getImageWidth(),
                grabber.getImageHeight(), grabber.getVideoBitrate(),
                grabber.getSampleRate(), grabber.getAudioBitrate());

        segments.forEach(segment -> {
            try {
                if (!segment.getGrabber().isCloseInputStream())
                    segment.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Instant end = Instant.now();
        System.out.println("Temps : " + Duration.between(start, end).getSeconds());
    }
}
