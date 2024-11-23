package cal.a24.javacv;

import cal.a24.model.Montage;
import cal.a24.model.Segment;
import lombok.SneakyThrows;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;


public class JavaCVPerformanceComplex {

    @SneakyThrows
    public static void main(String[] args) throws FFmpegFrameGrabber.Exception {

        String inputFile1 = "./src/main/resources/videos/bbb_sunflower_1080p_30fps_normal.mp4";

        String outputFile = "./output/javacv_performance_complex.mp4";

        Instant start = Instant.now();

        Segment segment1 = new Segment(inputFile1);
        System.out.println(segment1.getDuree());

        segment1.setTimestampDebut(10_000_000);
        segment1.setTimestampFin(20_000_000);

        FFmpegFrameGrabber grabber = segment1.getGrabber();

        List<Segment> segments = List.of(segment1, segment1, segment1, segment1, segment1);
        Montage montage = new Montage(segments);
        montage.export(outputFile, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getVideoBitrate(), grabber.getSampleRate(), grabber.getAudioBitrate());

        segments.forEach(segment -> {
            try {
                segment.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Instant end = Instant.now();
        System.out.println("Temps : " + Duration.between(start, end).getSeconds());
    }
}
