package cal.a24.javacv;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestClass {
    public class Segmentor {

        public static void openVideo(String filePath) {
            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath)) {
                grabber.start();
                System.out.println("Video opened: " + filePath);
                grabber.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static final int TOTAL_SEGMENTS = 5; // Example segment count

        private static void splitVideo(String filePath, ExecutorService executor) {
            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath)) {
                grabber.start();

                long totalDuration = grabber.getLengthInTime(); // Total video duration in microseconds
                long segmentDuration = totalDuration / TOTAL_SEGMENTS; // Duration of each segment

                for (int segment = 0; segment < TOTAL_SEGMENTS; segment++) {
                    final long startTimestamp = segment * segmentDuration; // Start timestamp for this segment
                    long endTimestamp; // End timestamp for this segment
                    // Last segment goes to the end of the video
                    if (segment == TOTAL_SEGMENTS - 1) {
                        endTimestamp = totalDuration;
                    } else {
                        endTimestamp = (segment + 1) * segmentDuration;
                    }

                    // Submit each segment processing task to the executor
                    int finalSegment = segment;
                    executor.submit(() -> {
                        try {
                            processSegment(filePath, finalSegment, startTimestamp, endTimestamp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

                grabber.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private static void processSegment(String filePath, int segmentNumber, long startTimestamp, long endTimestamp) {
            String outputFileName = "output/segment_" + segmentNumber + ".mp4"; // Define output file name pattern

            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath);
                 FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFileName, grabber.getImageWidth(), grabber.getImageHeight())) {

                grabber.start();
                grabber.setTimestamp(startTimestamp); // Seek to start timestamp

                // Configure recorder settings based on grabber
                configureRecorder(recorder, grabber);
                recorder.start();

                Frame frame;
                while ((frame = grabber.grabFrame()) != null && grabber.getTimestamp() < endTimestamp) {
                    recorder.record(frame); // Record frame to output file
                }

                recorder.stop();
                grabber.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private static void configureRecorder(FFmpegFrameRecorder recorder, FFmpegFrameGrabber grabber) {
            recorder.setFormat("mp4");
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setVideoBitrate(grabber.getVideoBitrate());
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioBitrate(grabber.getAudioBitrate());
            recorder.setAudioCodec(grabber.getAudioCodec());
        }
    }


    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String videoFilePath = "./src/main/resources/videos/48SecondesAvecSon.mp4"; // Adjust this to your video file path

        Segmentor.splitVideo(videoFilePath, executor);

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
