package src.gameObjects;

import java.awt.image.BufferedImage;

import src.graphics.Assets;

public enum Size {
    // Cuando se destruye el meteoro se divide en otro meteorito, cuando el tiny se destruye ya deja de existir
    BIG(2, Assets.meds), MED(2, Assets.smalls), SMALL(2, Assets.tinies), TINY(0, null); 
    public int quantity;
    public BufferedImage[] textures;

    private Size ( int quantity, BufferedImage[] textures ) {
        this.quantity = quantity;
        this.textures = textures;
    }
}