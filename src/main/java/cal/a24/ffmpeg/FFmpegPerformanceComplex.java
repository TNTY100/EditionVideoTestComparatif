package cal.a24.ffmpeg;

import javafx.util.Pair;
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
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class FFmpegPerformanceComplex {
    public static void main(String[] args) throws IOException {
        String inputFile1 = "./src/main/resources/videos/bbb_sunflower_1080p_30fps_normal.mp4";

        String outputFile = "./output/ffmpeg_performance_complex.mp4";

        Instant start = Instant.now();

        FFprobe probe = new FFprobe();
        FFmpeg ffmpeg = new FFmpeg();


        FFmpegProbeResult probeResult1 = probe.probe(inputFile1);
        List<FFmpegProbeResult> listInput = List.of(probeResult1, probeResult1, probeResult1, probeResult1, probeResult1);

        List<Pair<Double, Double>> debutFinList = listInput.stream()
                .flatMap(s -> s.getStreams().stream())
                .filter(s -> s.codec_type == FFmpegStream.CodecType.VIDEO)
                .map((_) -> new Pair<>(10.0, 20.0))
                .toList();


        FFmpegBuilder builder = ffmpeg.builder();
        builder.addInput(probeResult1);

        StringBuilder complexFilter = new StringBuilder();

        for (int i = 0; i < listInput.size(); i++) {
            // Ajout des paramètres pour la video
            complexFilter.append('[').append(0).append(":v]")
                    // Setup de la résolution pour les deux vidéos
                    .append("trim=start=")
                    .append(String.format(Locale.US,"%.2f", debutFinList.get(i).getKey()))
                    .append(":end=")
                    .append(String.format(Locale.US, "%.2f", debutFinList.get(i).getValue()))
                    .append(",setpts=PTS-STARTPTS")
                    // Association de la vidéo à un variable
                    .append("[v")
                    .append(i)
                    .append("];");

            // Ajout des paramètres pour l'audio
            complexFilter.append('[').append(0).append(":a]")
                    .append("atrim=start=")
                    .append(String.format(Locale.US, "%.2f", debutFinList.get(i).getKey()))
                    .append(":end=")
                    .append(String.format(Locale.US, "%.2f", debutFinList.get(i).getValue()))
                    .append(",asetpts=PTS-STARTPTS")
                    // Association de la vidéo à un variable
                    .append("[a")
                    .append(i)
                    .append("];");
        }

        // Build des paramètres avec la concaténation
        // Setup de la chaine
        for (int i = 0; i < listInput.size(); i++)
            complexFilter.append("[v")
                    .append(i)
                    .append("][a")
                    // setup de l'audio
                    .append(i)
                    .append("]");
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
