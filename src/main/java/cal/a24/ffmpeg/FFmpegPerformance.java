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

public class FFmpegPerformance {
    public static void main(String[] args) throws IOException {
        String inputFile1 = "./src/main/resources/videos/bbb_sunflower_2160p_60fps_normal.mp4";
        String inputFile2 = "./src/main/resources/videos/1hVideo.mp4";

        String outputFile = "./output/ffmpeg_performance.mp4";

        Instant start = Instant.now();

        FFprobe probe = new FFprobe();
        FFmpeg ffmpeg = new FFmpeg();


        FFmpegProbeResult probeResult1 = probe.probe(inputFile1);
        FFmpegProbeResult probeResult2 = probe.probe(inputFile2);
        List<FFmpegProbeResult> listInput = List.of(probeResult1, probeResult2);

        AtomicInteger index = new AtomicInteger(0);

        List<Pair<Double, Double>> debutFinList = listInput.stream()
                .flatMap(s -> s.getStreams().stream())
                .filter(s -> s.codec_type == FFmpegStream.CodecType.VIDEO)
                .map((s) -> {
                    if (index.getAndSet(index.get() + 1) == 0) {
                        return new Pair<>(s.duration/2, s.duration);
                    }
                    else {
                        return new Pair<>(0.0, s.duration/2);
                    }
                }).toList();


        FFmpegBuilder builder = ffmpeg.builder();
        listInput.forEach(builder::addInput);

        StringBuilder complexFilter = new StringBuilder();

        for (int i = 0; i < listInput.size(); i++) {
            // Ajout des paramètres pour la video
            complexFilter.append('[').append(i).append(":v]")
                    // Setup de la résolution pour les deux vidéos
                    .append("scale=")
                    .append(3840)
                    .append(':')
                    .append(2160)
                    //Trim
                    .append(",trim=start=")
                    .append(String.format(Locale.US,"%.2f", debutFinList.get(i).getKey()))
                    .append(":end=")
                    .append(String.format(Locale.US, "%.2f", debutFinList.get(i).getValue()))
                    .append(",setpts=PTS-STARTPTS")
                    // Association de la vidéo à un variable
                    .append("[v")
                    .append(i)
                    .append("];");

            // Ajout des paramètres pour l'audio
            complexFilter.append('[').append(i).append(":a]")
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
        complexFilter.append("concat=n=2:v=1:a=1[outv][outa]");

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
