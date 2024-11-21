package cal.a24.javacv;

import cal.a24.model.Montage;
import cal.a24.model.Segment;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


public class JavaCVPerformance {

    public static void main(String[] args) throws FFmpegFrameGrabber.Exception {

        String inputFile1 = "./src/main/resources/videos/bbb_sunflower_2160p_60fps_normal.mp4";
        String inputFile2 = "./src/main/resources/videos/1hVideo.mp4";

        String outputFile = "./output/javacv_performance.mp4";

        Instant start = Instant.now();

        Segment segment1 = new Segment(inputFile1);
        // Segment segment2 = new Segment(inputFile2);

        segment1.setTimestampDebut(segment1.getDuree() / 2);
        // segment2.setTimestampFin(segment2.getDuree() / 2);

        Montage montage = new Montage(List.of(segment1));
        montage.export(outputFile);

        Instant end = Instant.now();
        System.out.println("Temps : " + Duration.between(start, end));
    }
}
