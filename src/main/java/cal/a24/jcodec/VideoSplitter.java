package cal.a24.jcodec;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rational;
import org.jcodec.scale.Transform;
import org.jcodec.scale.Yuv420pToRgb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VideoSplitter {

    public static void splitVideoInHalf(File source, File output1, File output2) throws IOException, JCodecException {
        Transform transform = new Yuv420pToRgb();

        try (SeekableByteChannel sourceChannel = NIOUtils.readableChannel(source)) {
            FrameGrab grab = FrameGrab.createFrameGrab(sourceChannel);
            int totalFrames = grab.getVideoTrack().getMeta().getTotalFrames();
            int halfwayFrame = totalFrames / 2;

            // First Half
            try (SeekableByteChannel outputChannel1 = NIOUtils.writableChannel(output1)) {
                SequenceEncoder encoder1 = SequenceEncoder.createWithFps(outputChannel1, Rational.R(25, 1));
                grab.seekToFramePrecise(0);  // Start from the beginning
                for (int i = 0; i < halfwayFrame; i++) {
                    Picture yuvPic = grab.getNativeFrame();
                    if (yuvPic == null) break;
                    // Convert the frame to RGB using BufferedImage
                    Picture rgbPic = Picture.create(yuvPic.getWidth(), yuvPic.getHeight(), ColorSpace.RGB);
                    transform.transform(yuvPic, rgbPic);
                    encoder1.encodeNativeFrame(rgbPic);
                }
                encoder1.finish();
            }

            // Second Half
            try (SeekableByteChannel outputChannel2 = NIOUtils.writableChannel(output2)) {
                SequenceEncoder encoder2 = SequenceEncoder.createWithFps(outputChannel2, Rational.R(25, 1));
                grab.seekToFramePrecise(halfwayFrame);  // Start from halfway
                for (int i = halfwayFrame; i < totalFrames; i++) {
                    Picture yuvPic = grab.getNativeFrame();
                    if (yuvPic == null) break;
                    // Convert the frame to RGB using BufferedImage
                    Picture rgbPic = Picture.create(yuvPic.getWidth(), yuvPic.getHeight(), ColorSpace.RGB);
                    transform.transform(yuvPic, rgbPic);
                    encoder2.encodeNativeFrame(rgbPic);
                }
                encoder2.finish();
            }
        }
    }

    public static void main(String[] args) throws IOException, JCodecException {
        File source = new File("./src/main/resources/videos/48SecondesAvecSon.mp4");
        File output1 = new File("output/output_first_half.mp4");
        File output2 = new File("output/output_second_half.mp4");

        splitVideoInHalf(source, output1, output2);
    }
}

