package cal.a24.javacv;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.utils.Converters;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.Arrays;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

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
