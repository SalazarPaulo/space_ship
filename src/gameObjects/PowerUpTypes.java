package src.gameObjects;

import java.awt.Color;

/** Tipos de power-up; el icono de cada uno se dibuja con Java2D. */
public enum PowerUpTypes {
    SHIELD("SHIELD", new Color(65, 211, 255)),
    LIFE("+1 LIFE", new Color(255, 90, 115)),
    SCORE_X2("SCORE x2", new Color(250, 209, 67)),
    FASTER_FIRE("FAST FIRE", new Color(89, 146, 255)),
    SCORE_STACK("+1000 SCORE", new Color(207, 94, 255)),
    DOUBLE_GUN("DOUBLE GUN", new Color(255, 153, 64));

    public final String text;
    public final Color color;

    PowerUpTypes(String text, Color color) {
        this.text = text;
        this.color = color;
    }
}
