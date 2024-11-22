package cal.a24.javacv;

import cal.a24.model.Montage;
import cal.a24.model.Segment;
import lombok.SneakyThrows;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;


public class JavaCVPerformance {

    @SneakyThrows
    public static void main(String[] args) throws FFmpegFrameGrabber.Exception {

        String inputFile1 = "./src/main/resources/videos/bbb_sunflower_1080p_30fps_normal.mp4";
        String inputFile2 = "./src/main/resources/videos/48SecondesAvecSon.mp4";

        String outputFile = "./output/javacv_performance.mp4";

        Instant start = Instant.now();

        Segment segment1 = new Segment(inputFile1);
        Segment segment2 = new Segment(inputFile2);
        System.out.println(segment1.getDuree());

        segment1.setTimestampDebut(5_000_000);
        segment2.setTimestampDebut(5_000_000);
        segment2.setTimestampFin(20_000_000);

        List<Segment> segments = List.of(segment1, segment2);
        Montage montage = new Montage(segments);
        montage.export(outputFile);

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
