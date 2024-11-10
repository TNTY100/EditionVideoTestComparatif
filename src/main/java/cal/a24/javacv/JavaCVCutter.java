package cal.a24.javacv;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

public class JavaCVCutter {
    public static void main(String[] args) {
        String inputFile = "./src/main/resources/videos/48SecondesAvecSon.mp4";
        String outputFile = "output/javacv_first_half.mp4";
        String outputFile2 = "output/javacv_second_half.mp4";

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile)) {
            grabber.start();

            // Calculate the midpoint in frames
            double totalDuration = grabber.getLengthInTime() / 1_000_000.0; // Convert from microseconds to seconds
            double halfDuration = totalDuration / 2;

            // Set up the recorder with the same settings as the original video
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight())) {
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // or use grabber.getVideoCodec()
                recorder.setFormat(grabber.getFormat());
                recorder.setFrameRate(grabber.getFrameRate());
                recorder.setVideoBitrate(grabber.getVideoBitrate());

                // Set audio settings if the input video has audio
                if (grabber.getAudioChannels() > 0) {
                    recorder.setAudioChannels(grabber.getAudioChannels());
                    recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);  // AAC is commonly used
                    recorder.setSampleRate(grabber.getSampleRate());
                    recorder.setAudioBitrate(grabber.getAudioBitrate());
                }

                recorder.start();

                // Grab and record frames until we reach half of the video
                while (grabber.getTimestamp() / 1_000_000.0 < halfDuration) { // Convert from microseconds to seconds
                    Frame frame = grabber.grabFrame();
                    if (frame == null) break;
                    recorder.record(frame);
                }

                recorder.stop();
            }

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile2, grabber.getImageWidth(), grabber.getImageHeight())) {
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // or use grabber.getVideoCodec()
                recorder.setFormat(grabber.getFormat());
                recorder.setFrameRate(grabber.getFrameRate());
                recorder.setVideoBitrate(grabber.getVideoBitrate());

                // Set audio settings if the input video has audio
                if (grabber.getAudioChannels() > 0) {
                    recorder.setAudioChannels(grabber.getAudioChannels());
                    recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);  // AAC is commonly used
                    recorder.setSampleRate(grabber.getSampleRate());
                    recorder.setAudioBitrate(grabber.getAudioBitrate());
                }

                recorder.start();

                // Grab and record frames until we reach half of the video
                while (grabber.getTimestamp() < grabber.getLengthInTime()) { // Convert from microseconds to seconds
                    Frame frame = grabber.grabFrame();
                    if (frame == null) break;
                    recorder.record(frame);
                }

                recorder.stop();
            }

            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
