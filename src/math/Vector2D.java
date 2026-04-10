package src.math;

public class Vector2D {

    private double x, y;
    
    public double getX () {return x;}
    public double getY () {return y;}
    public void setX (double x) { this.x = x; }
    public void setY (double y) { this.y = y; }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D () {
        x = 0;
        y = 0;
    }
    public Vector2D add ( Vector2D v ) { return new Vector2D(x + v.getX(), y + v.getY()); }
    public Vector2D subtract ( Vector2D v ) { return new Vector2D(x - v.getX(), y - v.getY()); }
    public Vector2D scale ( double value ) { return new Vector2D( x*value, y*value );} // Para modificar la magnitud del vector posicion
    public Vector2D limit ( double value ) { 
        if ( getMagnitud() > value )
            return this.normalize().scale(value);
        return this;
    }
    public Vector2D normalize () {
        double magnitud =  getMagnitud();
        return new Vector2D(x / magnitud, y / magnitud);
    }
    public double getMagnitud () { return Math.sqrt(x*x + y*y); }
    public Vector2D setDirection ( double angle ) { 
        double magnitud =  getMagnitud();
        return new Vector2D(Math.cos(angle)*magnitud, Math.sin(angle)*magnitud); 
    }
    public double getAngle () { return Math.asin(y/getMagnitud());} // La magnitud es la hipotenusa
}