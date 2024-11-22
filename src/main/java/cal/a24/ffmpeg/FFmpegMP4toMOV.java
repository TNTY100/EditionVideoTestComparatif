package cal.a24.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.IOException;

public class FFmpegMP4toMOV {
    public static void main(String[] args) throws IOException, InterruptedException {
        String inputFile = "./src/main/resources/videos/48SecondesAvecSon.mp4";
        String outputFile = "./output/ffmpegmov.mov";
        String outputFile2 = "./output/ffmpegmov.mp4";

        FFmpeg ffmpeg = new FFmpeg();

        FFmpegBuilder builder = ffmpeg.builder()
                .setInput(inputFile)
                .addOutput(outputFile)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

        executor.createJob(builder).run();

    }
}
