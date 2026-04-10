package src.gameObjects;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public interface Graphics2D {

    void drawImage(BufferedImage player, AffineTransform at, Object object);

}
