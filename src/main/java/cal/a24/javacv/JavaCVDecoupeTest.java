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

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("./src/main/resources/videos/48SecondesAvecSon.mp4")
        ) {
            grabber.start();
            System.out.println(grabber.getImageWidth());
            System.out.println(grabber.getImageHeight());
            long totalDuration = grabber.getLengthInFrames();
            System.out.println("Duration " + totalDuration);

            long nbFrames = grabber.getLengthInFrames();
            long duree = nbFrames/4;
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("output/javaCVDecoupe.mp4", grabber.getImageWidth(), grabber.getImageHeight())){
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                recorder.setFormat(grabber.getFormat());
                recorder.setFrameRate(grabber.getFrameRate());
                recorder.setVideoBitrate(grabber.getVideoBitrate());

                // Set audio settings if the input video has audio
                if (grabber.getAudioChannels() > 0) {
                    recorder.setAudioChannels(grabber.getAudioChannels());
                    recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
                    recorder.setSampleRate(grabber.getSampleRate());
                    recorder.setAudioBitrate(grabber.getAudioBitrate());
                }
                System.out.println(recorder.getImageHeight());
                System.out.println(recorder.getImageWidth());
                // Start the recorder and ensure it started successfully
                recorder.start();


                for (int i = 0; i < duree; i++) {
                    Frame frame = grabber.grabFrame();
                    if (frame == null) break;
                    recorder.record(frame);
                }


                recorder.stop();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
