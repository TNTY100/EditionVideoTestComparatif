package cal.a24.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class FFmpegPerformanceLong {
    public static void main(String[] args) throws IOException {
        String inputFile1 = "./src/main/resources/videos/bbb_sunflower_1080p_30fps_normal.mp4";
        String inputFile2 = "./src/main/resources/videos/48SecondesAvecSon.mp4";

        String outputFile = "./output/ffmpeg_performance_long.mp4";

        Instant start = Instant.now();

        FFprobe probe = new FFprobe();
        FFmpeg ffmpeg = new FFmpeg();


        FFmpegProbeResult probeResult1 = probe.probe(inputFile1);
        FFmpegProbeResult probeResult2 = probe.probe(inputFile2);
        List<FFmpegProbeResult> listInput = List.of(probeResult1, probeResult2, probeResult1);

        FFmpegBuilder builder = ffmpeg.builder();
        listInput.forEach(builder::addInput);

        StringBuilder complexFilter = new StringBuilder();

        for (int i = 0; i < listInput.size(); i++) {
            // Ajout des paramètres pour la video
            complexFilter.append('[').append(i).append(":v]")
                    .append("scale=")
                    .append(1920)
                    .append(':')
                    .append(1080)
                    .append(",setsar=1")
                    // Association de la vidéo à un variable
                    .append("[v")
                    .append(i)
                    .append("];");
        }

        // Build des paramètres avec la concaténation
        // Setup de la chaine
        for (int i = 0; i < listInput.size(); i++)
            complexFilter.append("[v")
                    .append(i)
                    .append("][")
                    // setup de l'audio
                    .append(i)
                    .append(":a]");
        complexFilter.append("concat=n=").append(listInput.size()).append(":v=1:a=1[outv][outa]");

        builder = builder.setComplexFilter(complexFilter.toString());
        builder = builder.addOutput(outputFile)
                .addExtraArgs("-map", "[outv]")
                .addExtraArgs("-map", "[outa]")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

        executor.createJob(builder).run();

        Instant end = Instant.now();
        System.out.println("Temps : " + Duration.between(start, end).getSeconds());
    }
}
