package src.graphics;
import java.awt.image.BufferedImage;

public class Assets {

    public static BufferedImage player;
    public static BufferedImage speed; // Effects
    public static BufferedImage blueLaser, greenLaser, redLaser; // Lasers
    ////---- Meteors ----////
    public static BufferedImage [] bigs = new BufferedImage[4];
    public static BufferedImage [] meds = new BufferedImage[2];
    public static BufferedImage [] smalls = new BufferedImage[2];
    public static BufferedImage [] tinies = new BufferedImage[2];

	public static BufferedImage[] exp = new BufferedImage[9]; // explosion
    public static BufferedImage ufo;

    public static void init () {
        player = Loader.ImageLoader("/spaceshooter/PNG/player.png");
        speed = Loader.ImageLoader("/spaceshooter/PNG/Effects/fire08.png");
        blueLaser = Loader.ImageLoader("/spaceshooter/PNG/Lasers/laserBlue01.png");
        greenLaser = Loader.ImageLoader("/spaceshooter/PNG/Lasers/laserGreen11.png");
        redLaser = Loader.ImageLoader("/spaceshooter/PNG/Lasers/laserRed01.png");
        int i;
        for ( i = 0; i < bigs.length; i++ )
            bigs[i] = Loader.ImageLoader("/spaceshooter/PNG/Meteors/meteorGrey_big" + (i+1) + ".png");
        for ( i = 0; i < meds.length; i++ )
            meds[i] = Loader.ImageLoader("/spaceshooter/PNG/Meteors/meteorGrey_med" + (i+1) + ".png");
        for ( i = 0; i < smalls.length; i++ )
            smalls[i] = Loader.ImageLoader("/spaceshooter/PNG/Meteors/meteorGrey_small" + (i+1) + ".png");
        for ( i = 0; i < tinies.length; i++ )
            tinies[i] = Loader.ImageLoader("/spaceshooter/PNG/Meteors/meteorGrey_tiny" + (i+1) + ".png");
        for( i = 0; i < exp.length; i++)
			exp[i] = Loader.ImageLoader("/spaceshooter/PNG/Explosions/"+i+".png");
        ufo = Loader.ImageLoader("/spaceshooter/PNG/ufo.png");
    }
}
