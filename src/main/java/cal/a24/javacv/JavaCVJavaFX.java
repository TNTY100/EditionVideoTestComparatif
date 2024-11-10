package cal.a24.javacv;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bytedeco.javacv.JavaFXFrameConverter;

public class JavaCVJavaFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 600, 600);

        stage.setScene(scene);
        stage.setTitle("Sample Application");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
