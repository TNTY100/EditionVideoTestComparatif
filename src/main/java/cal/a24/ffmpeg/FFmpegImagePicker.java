package cal.a24.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.IOException;

public class FFmpegImagePicker {
    public static void main(String[] args) throws IOException {
        String inputFile1 = "./src/main/resources/videos/48SecondesAvecSon.mp4";
        String imageStart = "./output/FFmpegImageDebut.jpg";
        String imageEnd = "./output/FFmpegImageFin.png";

        FFmpeg ffmpeg = new FFmpeg();
        FFprobe probe = new FFprobe();

        FFmpegProbeResult probeResult = probe.probe(inputFile1);


        FFmpegBuilder builder = ffmpeg.builder()
                .setInput(probeResult)
                .addExtraArgs("-ss", "0")
                .addOutput(imageStart)
                .setFrames(1)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor();

        try {
            executor.createJob(builder).run();
        }
        catch (RuntimeException ignore) {}


        FFmpegBuilder builder1 = ffmpeg.builder()
                .setInput(probeResult)
                .addExtraArgs("-sseof", "-0.1")
                .addOutput(imageEnd)
                .setFrames(1)
                .done();

        try {
            executor.createJob(builder1).run();
        }
        catch (RuntimeException ignore) {}
    }
}
