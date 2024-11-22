package cal.a24.javacv;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

public class JavaCVMOVtoMP4 {
    public static void main(String[] args) throws FrameGrabber.Exception, FrameRecorder.Exception {
        String inputFile = "./src/main/resources/videos/48SecondesAvecSon.mov";
        String outputFile = "./output/javacvmp4.mp4";

        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputFile);
        frameGrabber.start();

        FFmpegFrameRecorder frameRecorder = new FFmpegFrameRecorder(outputFile,
                frameGrabber.getImageWidth(),
                frameGrabber.getImageHeight(),
                frameGrabber.getAudioChannels());

        frameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // or use grabber.getVideoCodec()
        frameRecorder.setFormat("mp4");
        frameRecorder.setFrameRate(frameGrabber.getFrameRate());
        frameRecorder.setVideoBitrate(frameGrabber.getVideoBitrate());

        // Set audio settings if the input video has audio
        if (frameGrabber.getAudioChannels() > 0) {
            frameRecorder.setAudioChannels(frameGrabber.getAudioChannels());
            frameRecorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);  // AAC is commonly used
            frameRecorder.setSampleRate(frameGrabber.getSampleRate());
            frameRecorder.setAudioBitrate(frameGrabber.getAudioBitrate());
        }
        frameRecorder.start();

        Frame frame;
        while ((frame = frameGrabber.grabFrame()) != null) {
            frameRecorder.record(frame);
        }

        frameRecorder.stop();
        frameRecorder.close();
        frameGrabber.stop();
        frameGrabber.close();
    }
}
