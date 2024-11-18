package cal.a24.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.IOException;

public class FFmpegCutter {

    public static void main(String[] args) throws IOException {
        String inputFile = "./src/main/resources/videos/48SecondesAvecSon.mp4";
        String outputFile = "output/ffmpeg_first_half.mp4";
        String outputFile2 = "output/ffmpeg_second_half.mp4";

        FFmpeg ffmpeg = new FFmpeg();
        FFprobe probe = new FFprobe();

        FFmpegProbeResult probeRes = probe.probe(inputFile);
        FFmpegStream fFmpegStream1 = probeRes.getStreams().stream()
                .filter(fFmpegStream -> fFmpegStream.codec_type == FFmpegStream.CodecType.VIDEO)
                .findFirst().orElseThrow();
        double duration = fFmpegStream1.duration;
        System.out.println(duration);

        // Partie 1
        FFmpegBuilder builder = ffmpeg.builder()
                .setInput(probeRes)
                // Début de la video
                .addExtraArgs("-ss", "0")
                // Durée du segment
                .addExtraArgs("-t", duration/2 + "")
                .addOutput(outputFile)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor();
        executor.createJob(builder).run();

        FFmpegBuilder builder1 = ffmpeg.builder()
                .setInput(probeRes)
                .addExtraArgs("-ss", duration/2 + "")
                .addOutput(outputFile2)
                .done();

        executor.createJob(builder1).run();
    }
}
