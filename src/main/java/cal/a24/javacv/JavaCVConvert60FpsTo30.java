package cal.a24.javacv;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

public class JavaCVConvert60FpsTo30 {
    public static void main(String[] args) {

        String inputFile = "./src/main/resources/videos/bbb_sunflower_2160p_60fps_normal.mp4";
        String outputFile = "./output/javacv_60to30.mp4";

        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
            grabber.start();

            FFmpegFrameFilter filter = new FFmpegFrameFilter("fps=fps=30",
                    "anull",
                    grabber.getImageWidth(),
                    grabber.getImageHeight(), grabber.getAudioChannels());
            filter.setSampleFormat(grabber.getSampleFormat());
            filter.setSampleRate(grabber.getSampleRate());
            filter.setPixelFormat(grabber.getPixelFormat());
            filter.setFrameRate(grabber.getFrameRate());
            filter.setSampleRate(grabber.getSampleRate());
            filter.setSampleFormat(grabber.getSampleFormat());
            filter.start();

            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile,
                    grabber.getImageWidth(),
                    grabber.getImageHeight(),
                    grabber.getAudioChannels());

            recorder.setSampleRate(filter.getSampleRate());
            recorder.setFrameRate(30);
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setFormat("mp4");
            recorder.setVideoBitrate(grabber.getVideoBitrate());
            recorder.setAudioBitrate(grabber.getAudioBitrate()/2);
            recorder.start();

            Frame capturedFrame;
            Frame pullFrame;
            while (recorder.getTimestamp() < 10_000_000) {
                try {
                    capturedFrame = grabber.grab();
                    if (capturedFrame == null) {
                        System.out.println("ERREUR");
                        break;
                    }

                    if (capturedFrame.image != null || capturedFrame.samples != null) {
                        filter.push(capturedFrame);
                    }
                    if ( (pullFrame = filter.pull()) != null) {
                        if(pullFrame.image != null || pullFrame.samples != null){
                            recorder.record(pullFrame);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            grabber.stop();
            grabber.close();
            filter.stop();
            filter.close();
            recorder.stop();
            recorder.release();
            recorder.close();

        } catch (FFmpegFrameGrabber.Exception e) {
            throw new RuntimeException(e);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        } catch (FFmpegFrameRecorder.Exception e) {
            throw new RuntimeException(e);
        } catch (FrameRecorder.Exception e) {
            throw new RuntimeException(e);
        } catch (FFmpegFrameFilter.Exception e) {
            throw new RuntimeException(e);
        } catch (FrameFilter.Exception e) {
            throw new RuntimeException(e);
        }
    }
}
