package src.math;

public class Vector2D {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D value) {
        this(value.x, value.y);
    }

    public Vector2D() {
        this(0, 0);
    }

    public Vector2D add(Vector2D value) {
        return new Vector2D(x + value.x, y + value.y);
    }

    public Vector2D subtract(Vector2D value) {
        return new Vector2D(x - value.x, y - value.y);
    }

    public Vector2D scale(double value) {
        return new Vector2D(x * value, y * value);
    }

    public Vector2D limit(double maximum) {
        return getMagnitude() > maximum ? normalize().scale(maximum) : new Vector2D(this);
    }

    public Vector2D normalize() {
        double magnitude = getMagnitude();
        return magnitude == 0 ? new Vector2D() : new Vector2D(x / magnitude, y / magnitude);
    }

    public double getMagnitude() {
        return Math.hypot(x, y);
    }

    // Alias usado por la versión anterior del proyecto.
    public double getMagnitud() {
        return getMagnitude();
    }

    public Vector2D setDirection(double angle) {
        double magnitude = getMagnitude();
        return new Vector2D(Math.cos(angle) * magnitude, Math.sin(angle) * magnitude);
    }

    public double getAngle() {
        return Math.atan2(y, x);
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
}
