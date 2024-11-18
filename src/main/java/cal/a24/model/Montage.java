package cal.a24.model;

import javafx.scene.image.Image;
import lombok.Data;

import java.util.List;

@Data
public class Montage {

    public List<Segment> segments;

    public Montage(List<Segment> segments) {
        setSegments(segments);
    }

    public Image getImageFXAtTimeStamp(long timestamp) {
        if (timestamp < 0 || timestamp > getDureeTotale()) {
            throw new RuntimeException("Le timestamp est à l'extérieur des limites");
        }

        for (Segment segment: segments) {
            if (timestamp < segment.getDuree()) {
                System.out.println("Getting picture at " + timestamp);
                try {
                    return segment.getImageFXAtTimestampInContent(timestamp);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            timestamp -= segment.getDuree();
        }
        return null;
    }

    public long getDureeTotale() {
            return segments.stream().mapToLong(Segment::getDuree).sum();
    }
}
