package cal.a24.jcodec;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rational;
import org.jcodec.scale.Yuv420jToRgb;
import org.jcodec.scale.Yuv420pToRgb;
import org.jcodec.scale.highbd.Yuv420jToRgbHiBD;

import java.io.File;
import java.io.IOException;

public class TestDecoupe {
    public static void main(String[] args) throws IOException, JCodecException {
        File file = new File("./src/main/resources/videos/12SecondesLondon.mp4");
        File output = new File("./output/jcodecDecoupe.mp4");
        int duree = 20;

        try (SeekableByteChannel inputChannel = NIOUtils.readableChannel(file);
             SeekableByteChannel outputChannel = NIOUtils.writableChannel(output);
        ) {
            FrameGrab frameGrab = FrameGrab.createFrameGrab(inputChannel);
            System.out.println(frameGrab.getMediaInfo().getDim().getHeight());
            System.out.println(frameGrab.getMediaInfo().getDim().getWidth());

            double fps = frameGrab.getVideoTrack().getMeta().getTotalDuration() / frameGrab.getVideoTrack().getMeta().getTotalFrames();
            int totalFrames = 30;
            System.out.println(totalFrames);
            SequenceEncoder encoder = SequenceEncoder.createWithFps(outputChannel, Rational.R((int)fps, 1));
            for (int i = 0; i < totalFrames; i++) {
                Picture yuvPic = frameGrab.getNativeFrame();
                System.out.println(yuvPic.getColor());

                Picture picture = Picture.create(yuvPic.getWidth(), yuvPic.getHeight(), ColorSpace.RGB);
                new Yuv420pToRgb().transform(yuvPic, picture);

                ColorSpace color = picture.getColor();

                if (!color.matches(ColorSpace.RGB)) {
                    System.out.println("No color" + color);
                }
                else {
                    encoder.encodeNativeFrame(picture);
                }
            }
            encoder.finish();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
