package cal.a24.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.IOException;

public class FFmpegConvert30FpsTo60 {
    public static void main(String[] args) throws IOException {

        String inputFile = "./src/main/resources/videos/48SecondesAvecSon.mp4";
        String outputFile = "./output/ffmpeg_30to60.mp4";


        FFmpeg ffmpeg = new FFmpeg();
        FFmpegBuilder builder = ffmpeg.builder()
                .addInput(inputFile)
                .setVideoFilter("fps=fps=60")
                .addOutput(outputFile)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

        executor.createJob(builder).run();
    }
}
