package cal.a24.javacv;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class JavaCVDecoupeTest {

    public static void main(String[] args) {

        // https://medium.com/@fajrimoad/efficient-video-splitting-with-ffmpeg-and-javacv-b19692db438d

        long duree = 300; // Temps en image de la découpe


        File file = new File("./src/main/resources/videos/48SecondesAvecSon.mp4");
        System.out.println(file.exists());
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("./src/main/resources/videos/48SecondesAvecSon.mp4");
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("output/javaCVDecoupe.mp4", grabber.getImageWidth(), grabber.getImageHeight())
        ) {
            grabber.start();
            System.out.println(grabber.getImageWidth());
            System.out.println(grabber.getImageHeight());
            long totalDuration = grabber.getLengthInFrames();
            System.out.println("Duration " + totalDuration);

            final long startTimestamp = 0;
            final long endTimestamp = duree;

            // grabber.setTimestamp(startTimestamp);

            // Configure le recorder
            // Partie VIDEO
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setVideoBitrate(grabber.getVideoBitrate());
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);

            // Partie Audio
            System.out.println(grabber.getAudioChannels());
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioBitrate(grabber.getAudioBitrate());

            recorder.start();


            for (int i = 0; i < duree; i++) {
                Frame frame = grabber.grabFrame();
                if (frame == null) break;
                recorder.record(frame);
            }


            recorder.stop();
            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
