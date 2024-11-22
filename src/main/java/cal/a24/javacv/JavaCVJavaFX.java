package cal.a24.javacv;

import cal.a24.model.Montage;
import cal.a24.model.Segment;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class JavaCVJavaFX extends Application {

    @Override
    public void start(Stage stage) throws URISyntaxException, FFmpegFrameGrabber.Exception {
        AtomicReference<Boolean> isPlay = new AtomicReference<>(false);
        AtomicReference<Boolean> isAppOn = new AtomicReference<>(true);

        Group root = new Group();
        Scene scene = new Scene(root, 600, 600);

        String inputFile1 = "./src/main/resources/videos/48SecondesAvecSon.mp4";
        String inputFile2 = "./src/main/resources/videos/12SecondesLondon.mp4";

        List<Segment> segments = List.of(new Segment(inputFile2), new Segment(inputFile1));
        Montage montage = new Montage(segments);
        ImageView imageView = new ImageView();

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(scene.getWidth());

        new Thread(() -> {
            long currentTime = 0L;
            while (currentTime < montage.getDureeTotale()) {
                imageView.setImage(montage.getImageFXAtTimeStamp(currentTime = currentTime + 100000));

                if (!isAppOn.get()) {
                    break;
                }

                while (!isPlay.get()) {
                    try {
                        Thread.sleep(1);
                        if (!isAppOn.get()) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            System.out.println("End of program");
            segments.forEach((segment -> {
                try {
                    segment.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
        }).start();

        VBox verticalBox = new VBox();
        verticalBox.setSpacing(5);
        verticalBox.setFillWidth(true);
        verticalBox.setAlignment(Pos.TOP_CENTER);
        verticalBox.autosize();

        Button buttonPause = new Button("Pause");

        buttonPause.setOnAction((_) -> {
            isPlay.set(!isPlay.get());
            buttonPause.setText(isPlay.get() ? "Pause" : "Play");
        });

         HBox buttonsBox = new HBox();
         buttonsBox.setAlignment(Pos.CENTER);
         buttonsBox.setStyle("-fx-border-color: grey; -fx-border-width: 2px");

        buttonsBox.getChildren().add(buttonPause);

        verticalBox.getChildren().add(imageView);
        verticalBox.getChildren().add(buttonsBox);

        root.getChildren().add(verticalBox);
        stage.setScene(scene);
        stage.setTitle("Sample Application");
        stage.show();
        stage.setOnCloseRequest(_ -> isAppOn.set(false));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
