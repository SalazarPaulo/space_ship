package src.graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Loader {

    public static BufferedImage ImageLoader (String path) {
        try {
            return ImageIO.read(Loader.class.getResource(path));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null; 
    }
}
