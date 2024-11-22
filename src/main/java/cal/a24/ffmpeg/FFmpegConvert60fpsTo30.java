package cal.a24.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FFmpegConvert60fpsTo30 {
    public static void main(String[] args) throws IOException {
        String inputFile = "./src/main/resources/videos/bbb_sunflower_2160p_60fps_normal.mp4";
        String outputFile = "./output/ffmpeg_60to30.mp4";


        FFmpeg ffmpeg = new FFmpeg();
        FFmpegBuilder builder = ffmpeg.builder()
                .addInput(inputFile)
                .setStartOffset(10, TimeUnit.SECONDS)
                .addExtraArgs("-t", "10")
                .setVideoFilter("fps=fps=30")
                .addOutput(outputFile)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

        executor.createJob(builder).run();
    }
}
