package cal.a24.javacv;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_videostab.NullLog;

public class JavaCVConcat {
    public static void main(String[] args) {
        String inputFile1 = "./src/main/resources/videos/48SecondesAvecSon.mp4";
        String inputFile2 = "./src/main/resources/videos/12SecondesLondon.mp4";
        String concatFile = "./output/JavaCVConcat.mp4";

        try (FrameGrabber grabber1 = new FFmpegFrameGrabber(inputFile1);
             FrameGrabber grabber2 = new FFmpegFrameGrabber(inputFile2)) {
            grabber1.start();
            grabber2.setImageHeight(grabber1.getImageHeight());
            grabber2.setImageWidth(grabber1.getImageWidth());
            grabber2.start();

            try (FrameRecorder recorder = new FFmpegFrameRecorder(concatFile, grabber1.getImageWidth(),
                    grabber1.getImageHeight())) {
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // or use grabber.getVideoCodec()
                recorder.setFormat(grabber1.getFormat());
                recorder.setFrameRate(grabber1.getFrameRate());
                recorder.setVideoBitrate(grabber1.getVideoBitrate());

                // Set audio settings if the input video has audio
                if (grabber1.getAudioChannels() > 0) {
                    recorder.setAudioChannels(grabber1.getAudioChannels());
                    recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);  // AAC is commonly used
                    recorder.setSampleRate(grabber1.getSampleRate());
                    recorder.setAudioBitrate(grabber1.getAudioBitrate());
                }

                recorder.start();

                Frame frame;
                while ((frame = grabber1.grabFrame()) != null) {
                    recorder.record(frame);
                }

                while ((frame = grabber2.grabFrame()) != null) {

                    recorder.record(frame);
                }

                recorder.stop();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            grabber1.stop();
            grabber2.stop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
