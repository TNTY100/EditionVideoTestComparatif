package cal.a24.ffmpeg;

import javafx.util.Pair;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.IOException;
import java.util.List;

public class FfmpegConcat {
    @SneakyThrows
    public static void main(String[] args) throws IOException, InterruptedException {
        String inputFile1 = "./src/main/resources/videos/48SecondesAvecSon.mp4";
        String inputFile2 = "./src/main/resources/videos/12SecondesLondon.mp4";
        List<String> listInput = List.of(inputFile1, inputFile2);
        String concatFile = "./output/FFmpegConcat.mp4";

        // Initialisation des CLI tools
        FFmpeg ffmpeg = new FFmpeg();
        FFprobe probe = new FFprobe();

        FFmpegBuilder builder = new FFmpegBuilder();

        Pair<Integer, Integer> smallestResolution = listInput.stream()
                .map(str ->{
                    try {
                        return probe.probe(str);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(e -> e.getStreams().stream())
                .filter(s -> s.codec_type == FFmpegStream.CodecType.VIDEO)
                .map(s -> new Pair<>(s.width, s.height))
                .reduce(new Pair<>(Integer.MAX_VALUE, Integer.MAX_VALUE),
                        (pair1, pair2) ->
                                new Pair<>(Integer.min(pair1.getKey(), pair2.getKey()),
                                        Integer.min(pair1.getValue(), pair2.getValue())));
        System.out.println(smallestResolution);
        // Ajoute les fichiers à concaténer
        listInput.forEach(builder::addInput);

        StringBuilder complexFilter = new StringBuilder();

        for (int i = 0; i < listInput.size(); i++) {
            // Ajout des paramètres pour la video
            complexFilter.append('[').append(i).append(":v]")
                    // Setup de la résolution pour les deux vidéos
                    .append("scale=")
                    .append(smallestResolution.getKey())
                    .append(':')
                    .append(smallestResolution.getValue())
                    // Association du stream à un variable
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

        complexFilter.append("concat=n=").append(listInput.size())
                .append(":v=1:a=1[outv][outa]");


        builder = builder.setComplexFilter(complexFilter.toString());

        builder = builder.addOutput(concatFile)
                .addExtraArgs("-map", "[outv]")
                .addExtraArgs("-map", "[outa]")
                .done();
        // Execute the FFmpeg command
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
        executor.createJob(builder).run();

    }
}
