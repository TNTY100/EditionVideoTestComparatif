package cal.a24.javacv;

import org.bytedeco.javacv.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;


public class JavaCVImagePicker {

    public static void main(String[] args) {
        String inputFile1 = "./src/main/resources/videos/48SecondesAvecSon.mp4";
        String imageStart = "./output/JavaCVImageDebut.jpg";
        String imageEnd = "./output/JavaCVImageFin.png";

        try (FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(inputFile1);
             FrameConverter<BufferedImage> converter = new Java2DFrameConverter();
             FileOutputStream outputStream = new FileOutputStream(imageStart)) {
            grabber1.start();

            Frame frame = grabber1.grabImage();
            BufferedImage bufferedImage = converter.convert(frame);
            ImageIO.write(bufferedImage, "jpg", outputStream);

            grabber1.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(inputFile1);
             FrameConverter<BufferedImage> converter = new Java2DFrameConverter();
             FileOutputStream outputStream = new FileOutputStream(imageEnd)) {
            grabber1.start();

            grabber1.setVideoFrameNumber(grabber1.getLengthInVideoFrames() - 1 );

            Frame frame = grabber1.grabImage();
            BufferedImage bufferedImage = converter.convert(frame);
            ImageIO.write(bufferedImage, "png", outputStream);

            grabber1.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
